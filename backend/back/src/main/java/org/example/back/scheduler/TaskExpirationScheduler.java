package org.example.back.scheduler;

import org.example.back.entity.Task;
import org.example.back.entity.TaskParticipant;
import org.example.back.entity.User;
import org.example.back.repository.TaskParticipantRepository;
import org.example.back.repository.TaskRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskExpirationScheduler {

    private static final int PENALTY_POINTS = 5;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AchievementService achievementService;

    /**
     * 每 10 分钟扫描一次：查找状态为 OPEN 或 IN_PROGRESS 且已过 deadline 的任务，
     * 对有 JOINED 参与者的任务执行取消。多人任务只有在已满员时才扣分，未满员不扣分。
     *
     * 使用现有仓库方法：TaskRepository.findByStatus(...) 和 TaskParticipantRepository.findByTaskId(...)
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    public void expireOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<String> targetStatuses = List.of("OPEN", "IN_PROGRESS");

        // 收集所有符合状态的任务，然后在内存中过滤 deadline
        List<Task> candidates = new ArrayList<>();
        for (String status : targetStatuses) {
            List<Task> byStatus = taskRepository.findByStatus(status);
            if (byStatus != null && !byStatus.isEmpty()) {
                candidates.addAll(byStatus);
            }
        }

        List<Task> overdue = candidates.stream()
                .filter(t -> t.getDeadline() != null && t.getDeadline().isBefore(now))
                .collect(Collectors.toList());

        for (Task task : overdue) {
            Long taskId = task.getId();
            // 获取所有参与记录并过滤出已接单的
            List<TaskParticipant> allParticipants = participantRepository.findByTaskId(taskId);
            List<TaskParticipant> joined = allParticipants == null ? List.of()
                    : allParticipants.stream()
                    .filter(p -> "JOINED".equals(p.getStatus()))
                    .collect(Collectors.toList());

            if (joined.isEmpty()) {
                // 没人接单，标记为取消
                task.setStatus("CANCELLED");
                task.setCurrentPeople(0);
                taskRepository.save(task);
                continue;
            }

            // 使用实体中存在的 needPeople 字段
            int required = task.getNeedPeople() != null ? task.getNeedPeople() : 1;
            boolean full = joined.size() >= required;

            // 扣分条件：单人任务（required==1）或多人任务且已满员
            boolean shouldPenalize = (required == 1) || full;

            if (shouldPenalize) {
                for (TaskParticipant p : joined) {
                    Long userId = p.getUserId();
                    User user = userRepository.findById(userId).orElse(null);
                    if (user != null) {
                        int current = user.getCreditScore() == null ? 0 : user.getCreditScore();
                        user.setCreditScore(Math.max(0, current - PENALTY_POINTS));
                        userRepository.save(user);
                        achievementService.recalculateUserAchievements(userId);
                    }
                }
            }

            // 将所有 JOINED 的参与记录标记为 CANCELLED
            for (TaskParticipant p : joined) {
                p.setStatus("CANCELLED");
                participantRepository.save(p);
            }

            // 更新任务状态与人数并保存
            task.setStatus("CANCELLED");
            task.setCurrentPeople(0);
            taskRepository.save(task);
        }
    }
}
