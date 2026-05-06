package org.example.back.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.HomeStatResp;
import org.example.back.dto.response.TaskVO;
import org.example.back.entity.Task;
import org.example.back.entity.TaskParticipant;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.TaskParticipantRepository;
import org.example.back.repository.TaskRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.PointsService;
import org.example.back.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskParticipantRepository taskParticipantRepository;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private UserRepository userRepository;

    // ================= 创建任务 =================
    @Override
    @Transactional
    public Long createTask(TaskCreateRequest request) {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        Task task = new Task();
        BeanUtils.copyProperties(request, task);

        task.setDeadline(parseDeadline(request.getDeadline()));
        task.setCurrentPeople(0);
        task.setStatus("OPEN");
        task.setPublisherId(userId);

        return taskRepository.save(task).getId();
    }

    // ================= 列表 =================
    @Override
    public List<TaskVO> list(int page, int size) {

        if (page < 0 || size <= 0 || size > 100) {
            throw new IllegalArgumentException("分页参数非法");
        }

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return taskRepository.findAllByStatus("OPEN", pageable)
                .getContent()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // ================= 抢任务 =================
    @Override
    @Transactional
    public TaskVO grabTask(Long taskId) {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        if (!"OPEN".equals(task.getStatus())) {
            throw new IllegalArgumentException("任务不可抢");
        }

        if (Objects.equals(task.getPublisherId(), userId)) {
            throw new IllegalArgumentException("不能抢自己发布的任务");
        }

        if (taskParticipantRepository.existsByTaskIdAndUserId(taskId, userId)) {
            throw new IllegalArgumentException("已抢过该任务");
        }

        int updated = taskRepository.incrementIfNotFull(taskId);
        if (updated == 0) {
            throw new IllegalArgumentException("任务已满");
        }

        try {
            TaskParticipant participant = TaskParticipant.builder()
                    .taskId(taskId)
                    .userId(userId)
                    .status("JOINED")
                    .build();

            taskParticipantRepository.save(participant);

        } catch (DataIntegrityViolationException e) {
            taskRepository.decrementIfNotEmpty(taskId);
            throw new IllegalArgumentException("已抢过该任务");
        }

        taskRepository.updateStatusIfFull(taskId);

        Task updatedTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        return toVO(updatedTask);
    }

    // ================= 完成任务 =================
    @Override
    @Transactional
    public void finishTask(Long taskId) {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        boolean isParticipant = taskParticipantRepository
                .existsByTaskIdAndUserId(taskId, userId);

        if (!isParticipant) {
            throw new IllegalArgumentException("无权限完成任务");
        }

        if (!"IN_PROGRESS".equals(task.getStatus())) {
            throw new IllegalArgumentException("任务状态非法");
        }

        int updated = taskRepository.updateStatusToFinishedIfInProgress(taskId);
        if (updated == 0) {
            throw new IllegalArgumentException("任务已完成");
        }

        Integer reward = task.getRewardPoints();
        if (reward == null || reward <= 0) return;

        List<TaskParticipant> participants =
                taskParticipantRepository.findByTaskId(taskId);

        for (TaskParticipant p : participants) {
            if (!"JOINED".equals(p.getStatus())) continue;

            p.setStatus("FINISHED");
            taskParticipantRepository.save(p);

            pointsService.addPoints(
                    p.getUserId(),
                    reward,
                    "完成任务",
                    "完成任务 " + task.getTitle()
            );
        }
    }

    // ================= 取消任务 =================
    @Override
    @Transactional
    public void cancelTask(Long taskId) {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        if (!Objects.equals(task.getPublisherId(), userId)) {
            throw new IllegalArgumentException("无权限取消任务");
        }

        if ("FINISHED".equals(task.getStatus())) {
            throw new IllegalArgumentException("任务已完成");
        }

        if ("CANCELLED".equals(task.getStatus())) {
            throw new IllegalArgumentException("任务已取消");
        }

        taskParticipantRepository.deleteByTaskId(taskId);

        task.setCurrentPeople(0);
        task.setStatus("CANCELLED");

        taskRepository.save(task);
    }

    // ================= ⭐ 关键修复：myTaskHistory =================
    @Override
    public List<TaskVO> myTaskHistory() {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        return taskRepository.findByPublisherId(userId)
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // ================= 搜索 =================
    @Override
    public List<TaskVO> findByTitle(String keywords) {

        if (keywords == null || keywords.length() > 50) {
            throw new IllegalArgumentException("关键词非法");
        }

        return taskRepository.findByTitleContaining(keywords)
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // ================= 详情 =================
    @Override
    public TaskVO findById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        return toVO(task);
    }

    // ================= 统计 =================
    @Override
    public HomeStatResp stats() {

        HomeStatResp resp = new HomeStatResp();

        List<Task> tasks = taskRepository.findAll();

        int users = (int) userRepository.count();
        int inProgress = 0, finished = 0;

        for (Task task : tasks) {
            if ("FINISHED".equals(task.getStatus())) finished++;
            else if ("IN_PROGRESS".equals(task.getStatus())) inProgress++;
        }

        resp.setInProgress(inProgress);
        resp.setFinished(finished);
        resp.setUsers(users);

        return resp;
    }

    // ================= VO转换（安全版） =================
    private TaskVO toVO(Task task) {

        User publisher = userRepository.findById(task.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        TaskVO vo = new TaskVO();

        vo.setTaskId(task.getId());
        vo.setTitle(task.getTitle());
        vo.setDescription(task.getDescription());
        vo.setNeedPeople(task.getNeedPeople());
        vo.setCurrentPeople(task.getCurrentPeople());
        vo.setStatus(task.getStatus());
        vo.setType(task.getType());
        vo.setPublisherId(task.getPublisherId());
        vo.setPublisherName(publisher.getUsername());
        vo.setRewardPoints(task.getRewardPoints());

        vo.setCreatedAt(task.getCreatedAt() == null ? null :
                task.getCreatedAt().atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli());

        vo.setDeadline(task.getDeadline() == null ? null :
                task.getDeadline().atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli());

        return vo;
    }

    // ================= 时间解析 =================
    private LocalDateTime parseDeadline(String deadlineStr) {

        if (deadlineStr == null || deadlineStr.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(deadlineStr);
        } catch (Exception ignored) {}

        try {
            return LocalDate.parse(deadlineStr).atStartOfDay();
        } catch (Exception e) {
            throw new IllegalArgumentException("deadline格式错误");
        }
    }
}