package org.example.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.request.RegisterRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.RegisterResponse;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void register_shouldReturnSuccessEnvelope() throws Exception {
        RegisterResponse resp = new RegisterResponse();
        resp.setUserId(1L);
        resp.setUsername("u");
        when(userService.register(any(RegisterRequest.class))).thenReturn(resp);

        RegisterRequest req = new RegisterRequest();
        req.setUsername("u");
        req.setPhone("13000000000");
        req.setPassword("p");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    void login_shouldReturnSuccessEnvelope() throws Exception {
        LoginResponse resp = new LoginResponse();
        resp.setUserId(2L);
        resp.setUsername("u2");
        resp.setToken("t");
        when(userService.login(any(LoginRequest.class))).thenReturn(resp);

        LoginRequest req = new LoginRequest();
        req.setPhone("13000000000");
        req.setPassword("p");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.token").value("t"));
    }

    @Test
    void info_whenServiceThrowsAuthException_shouldMapTo401() throws Exception {
        when(userService.getCurrentUser()).thenThrow(new AuthenticationException("用户未登录"));

        mockMvc.perform(get("/api/user/info"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Authentication error")));
    }
}

