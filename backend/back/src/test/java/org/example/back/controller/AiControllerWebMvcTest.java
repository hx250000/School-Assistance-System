package org.example.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back.config.SecurityConfig;
import org.example.back.dto.request.AiGenerateRequest;
import org.example.back.dto.response.AiGenerateResponse;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.service.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AiController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class AiControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiService aiService;

    @Test
    void generateDescription_shouldReturnDescription() throws Exception {
        AiGenerateRequest request = new AiGenerateRequest();
        request.setTitle("学习任务");
        request.setType("STUDY");

        AiGenerateResponse response = new AiGenerateResponse("这是一个学习任务的详细描述");

        when(aiService.generateTaskDescription(request)).thenReturn(response);

        mockMvc.perform(post("/api/ai/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.description").value("这是一个学习任务的详细描述"));
    }
}