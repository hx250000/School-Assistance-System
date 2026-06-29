package org.example.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back.config.SecurityConfig;
import org.example.back.dto.request.GrabTaskRequest;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.HomeStatResp;
import org.example.back.dto.response.TaskVO;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.service.TaskService;
import org.example.back.testutil.AuthTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class TaskControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private static final Long TEST_USER_ID = 1L;

    @Test
    void create_shouldReturnId() throws Exception {
        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(10L);

        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("t");
        req.setDescription("d");
        req.setType("STUDY");
        req.setNeedPeople(1);
        req.setDeadline("2026-04-27 10:00");

        mockMvc.perform(post("/api/task/create")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    void list_shouldReturnArray() throws Exception {
        TaskVO vo = new TaskVO();
        vo.setTaskId(1L);
        vo.setTitle("a");
        when(taskService.list(0, 10, "OPEN")).thenReturn(List.of(vo));

        mockMvc.perform(get("/api/task/list?page=0&size=10")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].taskId").value(1));
    }

    @Test
    void grab_shouldReturnTask() throws Exception {
        TaskVO vo = new TaskVO();
        vo.setTaskId(1L);
        vo.setStatus("OPEN");
        when(taskService.grabTask(1L)).thenReturn(vo);

        GrabTaskRequest req = new GrabTaskRequest();
        req.setTaskId(1L);

        mockMvc.perform(post("/api/task/grab")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.taskId").value(1));
    }

    @Test
    void finish_shouldReturnMessage() throws Exception {
        doNothing().when(taskService).finishTask(1L);

        mockMvc.perform(post("/api/task/1/finish")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务完成"));
    }

    @Test
    void getById_whenNotFound_shouldMapTo404() throws Exception {
        when(taskService.findById(eq(1L))).thenThrow(new ResourceNotFoundException("任务不存在！"));

        mockMvc.perform(get("/api/task/task?taskId=1")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("任务不存在")));
    }

    @Test
    void adminList_shouldReturnTasksWithStatus() throws Exception {
        TaskVO vo = new TaskVO();
        vo.setTaskId(1L);
        vo.setStatus("CLOSED");
        when(taskService.list(0, 10, "CLOSED")).thenReturn(List.of(vo));

        mockMvc.perform(get("/api/task/admin/list?page=0&size=10&status=CLOSED")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].taskId").value(1))
                .andExpect(jsonPath("$.data[0].status").value("CLOSED"));
    }

    @Test
    void participants_shouldReturnUserList() throws Exception {
        UserInfoVO user = new UserInfoVO();
        user.setId(1L);
        user.setUsername("testUser");
        when(taskService.participants(1L)).thenReturn(List.of(user));

        mockMvc.perform(get("/api/task/1/participants")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].username").value("testUser"));
    }

    @Test
    void cancel_shouldReturnMessage() throws Exception {
        doNothing().when(taskService).cancelTask(1L);

        mockMvc.perform(post("/api/task/1/cancel")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务已取消"));
    }

    @Test
    void history_shouldReturnTaskHistory() throws Exception {
        TaskVO vo = new TaskVO();
        vo.setTaskId(1L);
        vo.setTitle("已完成任务");
        when(taskService.myTaskHistory()).thenReturn(List.of(vo));

        mockMvc.perform(get("/api/task/history")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].taskId").value(1))
                .andExpect(jsonPath("$.data[0].title").value("已完成任务"));
    }

    @Test
    void joined_shouldReturnParticipatedTasks() throws Exception {
        TaskVO vo = new TaskVO();
        vo.setTaskId(1L);
        vo.setTitle("参与的任务");
        when(taskService.myParticipatedTasks()).thenReturn(List.of(vo));

        mockMvc.perform(get("/api/task/joined")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].taskId").value(1))
                .andExpect(jsonPath("$.data[0].title").value("参与的任务"));
    }

    @Test
    void search_shouldReturnMatchingTasks() throws Exception {
        TaskVO vo = new TaskVO();
        vo.setTaskId(1L);
        vo.setTitle("学习任务");
        when(taskService.findByTitle("学习")).thenReturn(List.of(vo));

        mockMvc.perform(get("/api/task/search?keyword=学习")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].taskId").value(1))
                .andExpect(jsonPath("$.data[0].title").value("学习任务"));
    }

    @Test
    void stats_shouldReturnHomeStats() throws Exception {
        HomeStatResp stats = new HomeStatResp();
        stats.setInProgress(5);
        stats.setFinished(10);
        stats.setUsers(100);
        when(taskService.stats()).thenReturn(stats);

        mockMvc.perform(get("/api/task/stats")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.inProgress").value(5))
                .andExpect(jsonPath("$.data.finished").value(10))
                .andExpect(jsonPath("$.data.users").value(100));
    }
}

