package org.example.back.service.impl;

import jakarta.transaction.Transactional;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.TaskVO;
import org.example.back.entity.Task;
import org.example.back.entity.TaskParticipant;
import org.example.back.repository.TaskParticipantRepository;
import org.example.back.repository.TaskRepository;
import org.example.back.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // TODO: 从 JWT 中获取真实发布人 ID
        task.setPublisherId(1L);

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
    public void grabTask(Long taskId, Long userId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (!"OPEN".equals(task.getStatus())) {
            throw new RuntimeException("任务不可抢");
        }

        if (task.getCurrentPeople() >= task.getNeedPeople()) {
            throw new RuntimeException("人数已满");
        }

        // 🚨 关键：防止重复抢单（强烈建议加）
        boolean exists = taskParticipantRepository
                .existsByTaskIdAndUserId(taskId, userId);

        if (exists) {
            throw new RuntimeException("你已经抢过该任务");
        }

        // =========================
        // ✅ 1. 插入参与者记录（新增核心逻辑）
        // =========================
        TaskParticipant participant = TaskParticipant.builder()
                .taskId(taskId)
                .userId(userId)
                .status("JOINED")
                .build();

        taskParticipantRepository.save(participant);

        // =========================
        // ✅ 2. 更新任务人数
        // =========================
        task.setCurrentPeople(task.getCurrentPeople() + 1);

        if (task.getCurrentPeople().equals(task.getNeedPeople())) {
            task.setStatus("IN_PROGRESS");
        }

        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void finishTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        task.setStatus("FINISHED");
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
        vo.setPublisherId(task.getPublisherId());
        vo.setCreateTime(task.getCreatedAt() == null ? null : task.getCreatedAt().toString());
        return vo;
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
            throw new RuntimeException("deadline 格式错误: " + deadlineStr);
        }
    }


}