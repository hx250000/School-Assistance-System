package org.example.back.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.HomeStatResp;
import org.example.back.dto.response.TaskVO;
import org.example.back.entity.*;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceConflictException;
import org.example.back.repository.TaskParticipantRepository;
import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.*;
import org.example.back.service.AchievementService;
import org.example.back.service.PointsService;
import org.example.back.service.TaskService;
import org.example.back.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskParticipantRepository taskParticipantRepository;
    @Autowired private PointsService pointsService;
    @Autowired private AchievementService achievementService;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    private static final int PENALTY_POINTS = 5;

    // ================= create =================
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

        Long taskId = taskRepository.save(task).getId();
        achievementService.recalculateUserAchievements(userId);
        return taskId;
    }

    // ================= list =================
    @Override
    public List<TaskVO> list(int page, int size) {
        log.info("list tasks, page={}, size={}",page,size);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 传入状态过滤条件
        List<TaskVO> resp=taskRepository.findAllByStatus("OPEN", pageable)
                .getContent()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        log.info("list resp: "+ resp);
        return resp;
    }

    // ================= grab =================
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
            throw new ResourceConflictException("任务不可抢");
        }
      
//        if (task.getPublisherId() != null && task.getPublisherId().equals(userId)) {
//            throw new ResourceConflictException("不能抢自己发布的任务");
//        }

//        if (taskParticipantRepository.existsByTaskIdAndUserId(taskId, userId)) {
//            throw new ResourceConflictException("你已经抢过该任务！");
//        }

        int updated = taskRepository.incrementIfNotFull(taskId);
        if (updated == 0) {
            throw new ResourceConflictException("任务已满");
        }

        try {

            TaskParticipant p = TaskParticipant.builder()
                    .taskId(taskId)
                    .userId(userId)
                    .status("JOINED")
                    .build();

            taskParticipantRepository.save(p);
            log.info("task participant: " + p);

        } catch (DataIntegrityViolationException e){
            log.info("Already have task participant: " + task);
            throw new ResourceConflictException("你已经抢过该任务！");
        }

        taskRepository.updateStatusIfFull(taskId);

        return toVO(taskRepository.findById(taskId).get());
    }

    // ================= finish =================
    @Override
    @Transactional
    public void finishTask(Long taskId) {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        log.info("finishtask, taskId="+taskId+", userId="+userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

//        boolean ok = taskParticipantRepository.existsByTaskIdAndUserId(taskId, userId);
//        if (!ok) {
//            throw new IllegalArgumentException("无权限完成任务");
//        }

        if (!"IN_PROGRESS".equals(task.getStatus())) {
            throw new ResourceConflictException("任务状态非法");
        }

        task.setStatus("FINISHED");
        taskRepository.save(task);

        Integer reward = task.getRewardPoints();
        if (reward == null || reward <= 0) return;

        List<TaskParticipant> list = taskParticipantRepository.findByTaskId(taskId);

        for (TaskParticipant p : list) {
            if (!"JOINED".equals(p.getStatus())) continue;

            p.setStatus("FINISHED");
            taskParticipantRepository.save(p);

            pointsService.addPoints(
                    p.getUserId(),
                    reward,
                    "完成任务",
                    "完成任务 " + task.getTitle()
            );

            achievementService.recalculateUserAchievements(p.getUserId());
        }
    }

    // ================= cancel =================
    @Override
    @Transactional
    public void cancelTask(Long taskId) {
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在: " + taskId));

        TaskParticipant participant = taskParticipantRepository
                .findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("当前用户未加入该任务"));

        if ("FINISHED".equals(participant.getStatus())) {
            throw new ResourceConflictException("已完成的参与记录不能取消");
        }
        if ("CANCELLED".equals(participant.getStatus())) {
            throw new ResourceConflictException("已取消的参与记录无法重复取消");
        }

        // 标记参与记录为已取消
        participant.setStatus("CANCELLED");
        taskParticipantRepository.save(participant);

        // 减少已参与人数（防止负值）
        if (task.getCurrentPeople() == null) {
            task.setCurrentPeople(0);
        } else if (task.getCurrentPeople() > 0) {
            task.setCurrentPeople(task.getCurrentPeople() - 1);
        }
        taskRepository.save(task);

        // 在任务开始前 24 小时内取消，扣除 5 点信用分（针对接任务的人）
        if (task.getDeadline() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cutoff = task.getDeadline().minusHours(24);
            if (!now.isBefore(cutoff)) { // now >= cutoff 说明在 24 小时内取消
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("用户不存在: " + userId));
                int newScore = Math.max(0, (user.getCreditScore() == null ? 0 : user.getCreditScore()) - 5);
                user.setCreditScore(newScore);
                userRepository.save(user);
            }
        }
    }

    // ================= history =================
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

    // ================= participated tasks =================
    @Override
    public List<TaskVO> myParticipatedTasks() {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        return taskParticipantRepository.findByUserId(userId)
                .stream()
                .map(p -> taskRepository.findById(p.getTaskId())
                        .orElse(null))
                .filter(task -> task != null)
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // ================= search =================
    @Override
    public List<TaskVO> findByTitle(String keywords) {

        return taskRepository.findByTitleContaining(keywords)
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // ================= detail =================
    @Override
    public TaskVO findById(Long id) {

        Task t = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("任务不存在"));

        return toVO(t);
    }

    // ================= stats =================
    @Override
    public HomeStatResp stats() {

        HomeStatResp r = new HomeStatResp();

        int users = (int) userRepository.count();
        int open = 0, fin = 0;

        for (Task t : taskRepository.findAll()) {
            if ("FINISHED".equals(t.getStatus())) fin++;
            else if ("OPEN".equals(t.getStatus())) open++;
        }

        r.setUsers(users);
        r.setFinished(fin);
        r.setInProgress(open);

        return r;
    }

    // ================= VO =================
    private TaskVO toVO(Task task) {

        User u = userRepository.findById(task.getPublisherId())
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
        vo.setPublisherName(u.getUsername());
        vo.setRewardPoints(task.getRewardPoints());

        vo.setCreatedAt(task.getCreatedAt().atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli());

        vo.setDeadline(task.getDeadline().atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli());

        return vo;
    }

    // ================= deadline =================
    private LocalDateTime parseDeadline(String s) {

        if (s == null || s.isBlank()) return null;

        s = s.trim();

        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
        };

        for (String p : patterns) {
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(p));
            } catch (Exception ignored) {}
        }

        try {
            return LocalDate.parse(s).atStartOfDay();
        } catch (Exception e) {
            throw new IllegalArgumentException("deadline格式错误");
        }
    }
}