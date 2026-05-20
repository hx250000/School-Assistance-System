package org.example.back.service;

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
import org.example.back.service.impl.TaskServiceImpl;
import org.example.back.exception.ResourceConflictException;
import org.example.back.testutil.AuthTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskParticipantRepository taskParticipantRepository;

    @Mock
    private PointsService pointsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @AfterEach
    void tearDown() {
        AuthTestUtil.clear();
    }

    @Test
    void createTask_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("t");
        req.setType("STUDY");
        req.setNeedPeople(1);

        assertThatThrownBy(() -> taskService.createTask(req))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void createTask_shouldSetPublisherStatusAndParseDeadline_andReturnId() {
        AuthTestUtil.setCurrentUserId(5L);
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("测试任务");
        req.setDeadline("2026-04-27 10:30");

        Task saved = new Task();
        saved.setId(99L);
        // 源代码中 save 后会获取 ID
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Long id = taskService.createTask(req);
        
        assertThat(id).isEqualTo(99L);
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        
        Task toSave = captor.getValue();
        assertThat(toSave.getPublisherId()).isEqualTo(5L);
        assertThat(toSave.getStatus()).isEqualTo("OPEN");
        assertThat(toSave.getCurrentPeople()).isEqualTo(0);
    }

    @Test
    void list_shouldMapTasksToVOs() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setTitle("a");
        t1.setDescription("da");
        t1.setNeedPeople(1);
        t1.setCurrentPeople(0);
        t1.setStatus("OPEN");
        t1.setType("STUDY");
        t1.setPublisherId(2L);
        t1.setRewardPoints(10);
        t1.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        t1.setDeadline(LocalDateTime.of(2026, 1, 2, 0, 0));

        Page<Task> page = new PageImpl<>(List.of(t1));
        when(taskRepository.findAllByStatus(eq("OPEN"), any(Pageable.class))).thenReturn(page);

        // 3. 同时别忘了 toVO 依赖 User
        User mockUser = new User();
        mockUser.setId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

        List<TaskVO> vos = taskService.list(0, 10);
        assertThat(vos).hasSize(1);
        assertThat(vos.get(0).getTaskId()).isEqualTo(1L);
        assertThat(vos.get(0).getTitle()).isEqualTo("a");
    }

    @Test
    void grabTask_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();

        assertThatThrownBy(() -> taskService.grabTask(1L))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void grabTask_whenTaskNotFound_shouldThrowResourceNotFoundException() {
        AuthTestUtil.setCurrentUserId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.grabTask(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("任务不存在");
    }

    @Test
    void grabTask_whenAlreadyJoined_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(1L);
        
        Task t = new Task();
        t.setId(10L);
        t.setPublisherId(2L);
        t.setStatus("OPEN");
        
        when(taskRepository.findById(10L)).thenReturn(Optional.of(t));
        when(taskRepository.incrementIfNotFull(10L)).thenReturn(1);
        
        when(taskParticipantRepository.save(any(TaskParticipant.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        assertThatThrownBy(() -> taskService.grabTask(10L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("你已经抢过该任务");
    }

    @Test
    void cancelTask_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();

        assertThatThrownBy(() -> taskService.cancelTask(1L))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void cancelTask_whenStatusFinished_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(1L);
        
        Task task = new Task();
        task.setId(1L);
        task.setPublisherId(1L);
        task.setStatus("FINISHED");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.cancelTask(1L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("已完成的任务不能取消");
    }

    @Test
    void cancelTask_whenStatusCancelled_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(1L);
        
        Task task = new Task();
        task.setId(1L);
        task.setPublisherId(1L);
        task.setStatus("CANCELLED");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.cancelTask(1L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("任务已取消");
    }

    @Test
    void cancelTask_whenSuccess_shouldUpdateStatusAndDeleteParticipants() {
        AuthTestUtil.setCurrentUserId(1L);
        
        Task task = new Task();
        task.setId(1L);
        task.setPublisherId(1L);
        task.setStatus("OPEN");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.cancelTask(1L);
        
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("CANCELLED");
        assertThat(captor.getValue().getCurrentPeople()).isEqualTo(0);
    }

    @Test
    void findByTitle_shouldReturnMatchingTasks() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setTitle("Java学习任务");
        t1.setPublisherId(1L);
        t1.setCreatedAt(LocalDateTime.now());
        t1.setDeadline(LocalDateTime.now().plusDays(1));
        
        Task t2 = new Task();
        t2.setId(2L);
        t2.setTitle("Python任务");
        t2.setPublisherId(2L);
        t2.setCreatedAt(LocalDateTime.now());
        t2.setDeadline(LocalDateTime.now().plusDays(1));

        when(taskRepository.findByTitleContaining("学习")).thenReturn(List.of(t1));
        
        User mockUser = new User();
        mockUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        List<TaskVO> vos = taskService.findByTitle("学习");
        
        assertThat(vos).hasSize(1);
        assertThat(vos.get(0).getTitle()).isEqualTo("Java学习任务");
    }

    @Test
    void stats_shouldReturnHomeStatResp() {
        User u1 = new User();
        u1.setId(1L);
        when(userRepository.count()).thenReturn(2L);

        Task t1 = new Task();
        t1.setStatus("IN_PROGRESS");
        Task t2 = new Task();
        t2.setStatus("FINISHED");
        Task t3 = new Task();
        t3.setStatus("OPEN");
        
        when(taskRepository.findAll()).thenReturn(List.of(t1, t2, t3));

        HomeStatResp resp = taskService.stats();
        
        assertThat(resp.getUsers()).isEqualTo(2);
        assertThat(resp.getInProgress()).isEqualTo(1);
        assertThat(resp.getFinished()).isEqualTo(1);
    }

    @Test
    void grabTask_whenStatusNotOpen_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(1L);
        Task t = new Task();
        t.setId(1L);
        t.setStatus("CANCELLED");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> taskService.grabTask(1L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("任务不可抢");
    }


    @Test
    void grabTask_whenFull_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(1L);
        Task t = new Task();
        t.setId(1L);
        t.setStatus("OPEN");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(t));
        when(taskRepository.incrementIfNotFull(1L)).thenReturn(0);

        assertThatThrownBy(() -> taskService.grabTask(1L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("任务已满");
    }

    @Test
    void grabTask_whenSuccess_shouldCreateParticipant_updateStatusAndReturnLatestTask() {
        // 1. 设置当前操作用户
        AuthTestUtil.setCurrentUserId(1L);

        // 2. 准备任务数据，注意设置 PublisherId
        Task t = new Task();
        t.setId(1L);
        t.setStatus("OPEN");
        t.setPublisherId(100L); // 假设发布者 ID 是 100

        // 3. 模拟参与逻辑
        when(taskRepository.incrementIfNotFull(1L)).thenReturn(1);

        // 4. 模拟 grabTask 结束前重新查询到的最新任务状态
        Task latest = new Task();
        latest.setId(1L);
        latest.setStatus("OPEN"); // 还未满员，状态仍是 OPEN
        latest.setPublisherId(100L); // 保持发布者 ID 一致
        latest.setCreatedAt(LocalDateTime.now()); // 修复 getCreatedAt() 为空的问题
        latest.setDeadline(LocalDateTime.of(2026,12,31,23,59,59));
        latest.setTitle("测试任务");
        latest.setNeedPeople(5);
        latest.setCurrentPeople(1);

        // 模拟 findById 的两次调用（第一次在逻辑开始，第二次在 toVO 转换前）
        when(taskRepository.findById(1L)).thenReturn(Optional.of(t), Optional.of(latest));

        // 模拟 User 的存在，防止 toVO 抛出 ResourceNotFoundException ---
        org.example.back.entity.User mockUser = new org.example.back.entity.User();
        mockUser.setId(100L);
        mockUser.setUsername("testPublisher");
        // 只要是查询这个 ID，就返回这个 Mock 用户
        when(userRepository.findById(100L)).thenReturn(Optional.of(mockUser));
        // -----------------------------------------------------------------------

        // 执行
        TaskVO res = taskService.grabTask(1L);

        // 断言
        assertThat(res).isNotNull();
        verify(taskParticipantRepository).save(any(TaskParticipant.class));
        verify(taskRepository).updateStatusIfFull(1L);
    }

    @Test
    void finishTask_whenStatusNotInProgress_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(1L);
        Task task = new Task();
        task.setStatus("OPEN"); // 源代码要求必须是 IN_PROGRESS 才能完成
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.finishTask(1L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("任务状态非法");
    }

    @Test
    void finishTask_shouldUpdateTaskStatus_andAwardPointsToJoinedParticipants() {
        AuthTestUtil.setCurrentUserId(1L);
        Task task = new Task();
        task.setId(1L);
        task.setTitle("T");
        task.setStatus("IN_PROGRESS");
        task.setPublisherId(1L);
        task.setRewardPoints(10);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskParticipant p1 = new TaskParticipant();
        p1.setId(1L);
        p1.setTaskId(1L);
        p1.setUserId(2L);
        p1.setStatus("JOINED");
        TaskParticipant p2 = new TaskParticipant();
        p2.setId(2L);
        p2.setTaskId(1L);
        p2.setUserId(3L);
        p2.setStatus("FINISHED");

        when(taskParticipantRepository.findByTaskId(1L)).thenReturn(List.of(p1, p2));

        taskService.finishTask(1L);

        verify(taskRepository).save(argThat(t -> "FINISHED".equals(t.getStatus())));
        verify(taskParticipantRepository).save(argThat(p -> "FINISHED".equals(p.getStatus()) && p.getUserId().equals(2L)));
        verify(pointsService).addPoints(eq(2L), eq(10), anyString(), anyString());
        verify(pointsService, never()).addPoints(eq(3L), anyInt(), anyString(), anyString());
    }

    @Test
    void cancelTask_whenNotPublisher_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(2L);
        Task task = new Task();
        task.setId(1L);
        task.setPublisherId(1L);
        task.setStatus("OPEN");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.cancelTask(1L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("仅发布者可取消");
    }

    @Test
    void myTaskHistory_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();

        assertThatThrownBy(() -> taskService.myTaskHistory())
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void findById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("任务不存在");
    }
}

