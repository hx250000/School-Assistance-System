package org.example.back.service.impl;

import jakarta.transaction.Transactional;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.TaskVO;
import org.example.back.entity.Task;
import org.example.back.entity.TaskParticipant;
import org.example.back.exception.AuthenticationException;
import org.example.back.repository.TaskParticipantRepository;
import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.TaskRepository;
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
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskParticipantRepository taskParticipantRepository;

    @Override
    @Transactional
    public Long createTask(TaskCreateRequest request) {

        Task task = new Task();
        BeanUtils.copyProperties(request, task);

//        // BeanUtils 不会自动把 String -> LocalDateTime 做可靠转换
//        // 同时 TaskCreateRequest 目前没有 getter，因此用反射读取 deadline 字段再解析。
//        String deadlineStr = extractDeadlineStr(request);
//        task.setDeadline(parseDeadline(deadlineStr));
        String deadlinestr = request.getDeadline();
        task.setDeadline(parseDeadline(deadlinestr));

        task.setCurrentPeople(0);
        task.setStatus("OPEN");

        // 从JWT中获取真实发布人ID
        Long publisherId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (publisherId == null) {
            throw new AuthenticationException("用户未登录，无法发布任务");
        }
        task.setPublisherId(publisherId);

        Task saved = taskRepository.save(task);
        return saved.getId();
    }

    @Override
    public List<TaskVO> list(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return taskRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Task grabTask(Long taskId) {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        // 先查任务（校验状态）
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        if (!"OPEN".equals(task.getStatus())) {
            throw new IllegalArgumentException("任务不可抢");
        }

        if (taskParticipantRepository.existsByTaskIdAndUserId(taskId, userId)){
            throw new IllegalArgumentException("你已经抢过该任务！");
        }

        // 再抢名额（原子操作）
        int updated = taskRepository.incrementIfNotFull(taskId);
        if (updated == 0) {
            throw new IllegalArgumentException("任务已满");
        }

        // 插入参与记录（用唯一索引兜底！）
        try {
            TaskParticipant participant = TaskParticipant.builder()
                    .taskId(taskId)
                    .userId(userId)
                    .status("JOINED")
                    .build();

            taskParticipantRepository.save(participant);

        } catch (DataIntegrityViolationException e){
            taskRepository.decrementIfNotEmpty(taskId);
            throw new IllegalArgumentException("你已经抢过该任务！");
        } catch (Exception e) {
            taskRepository.decrementIfNotEmpty(taskId);
            throw new RuntimeException("系统异常，请稍后再试");
        }

        //该修改有竞态条件，采用原子化操作
//        // 重新查（拿最新人数）
//        Task latestTask = taskRepository.findById(taskId).get();
//
//        // 满员 → 修改状态
//        if (latestTask.getCurrentPeople() >= latestTask.getNeedPeople()) {
//            latestTask.setStatus("IN_PROGRESS");
//            taskRepository.save(latestTask);
//        }
        taskRepository.updateStatusIfFull(taskId);

        Task taskGrabbed=taskRepository.findById(taskId).get();
        return taskGrabbed;
    }

    @Override
    @Transactional
    public void finishTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务"+taskId+"不存在"));

        task.setStatus("FINISHED");
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void cancelTask(Long taskId) {
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        if (!userId.equals(task.getPublisherId())) {
            throw new IllegalArgumentException("仅发布者可取消该任务");
        }

        String status = task.getStatus();
        if ("FINISHED".equals(status)) {
            throw new IllegalArgumentException("任务已完成，无法取消");
        }
        if ("CANCELLED".equals(status)) {
            throw new IllegalArgumentException("任务已取消");
        }

        taskParticipantRepository.deleteByTaskId(taskId);
        task.setCurrentPeople(0);
        task.setStatus("CANCELLED");
        taskRepository.save(task);
    }

    private TaskVO toVO(Task task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setTitle(task.getTitle());
        vo.setDescription(task.getDescription());
        vo.setNeedPeople(task.getNeedPeople());
        vo.setCurrentPeople(task.getCurrentPeople());
        vo.setStatus(task.getStatus());
        vo.setType(task.getType());
        vo.setPublisherId(task.getPublisherId());

//        vo.setRewardMoney(task.getRewardMoney());
        vo.setRewardPoints(task.getRewardPoints());

        vo.setCreatedAt(task.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        vo.setDeadline(task.getDeadline().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return vo;
    }

    @Override
    public List<Task> myTaskHistory(){
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }
        List<Task> taskhistory = taskRepository.findByPublisherId(userId);
        return taskhistory;
    }

    private LocalDateTime parseDeadline(String deadlineStr) {
        if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
            return null;
        }

        String s = deadlineStr.trim();
        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(s, formatter);
            } catch (DateTimeParseException ignored) {
                // try next
            }
        }

        // 兜底：只传日期时，默认时间为 00:00:00
        try {
            LocalDate date = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
            return date.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            throw new IllegalArgumentException("deadline 格式错误: " + deadlineStr);
        }
    }


}