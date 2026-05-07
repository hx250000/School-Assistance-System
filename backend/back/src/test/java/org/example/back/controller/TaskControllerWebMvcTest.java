package org.example.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back.dto.request.GrabTaskRequest;
import org.example.back.dto.request.TaskCreateRequest;
import org.example.back.dto.response.TaskVO;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.service.TaskService;
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
@Import(GlobalExceptionHandler.class)
class TaskControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

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
        when(taskService.list(0, 10)).thenReturn(List.of(vo));

        mockMvc.perform(get("/api/task/list?page=0&size=10"))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.taskId").value(1));
    }

    @Test
    void finish_shouldReturnMessage() throws Exception {
        doNothing().when(taskService).finishTask(1L);

        mockMvc.perform(post("/api/task/1/finish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("任务完成"));
    }

    @Test
    void getById_whenNotFound_shouldMapTo404() throws Exception {
        when(taskService.findById(eq(1L))).thenThrow(new ResourceNotFoundException("任务不存在！"));

        mockMvc.perform(get("/api/task/task?taskId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("任务不存在")));
    }
}

