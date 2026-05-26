package org.example.back.service;

import com.alibaba.fastjson.JSONObject;
import org.example.back.dto.request.AiGenerateRequest;
import org.example.back.dto.response.AiGenerateResponse;
import org.example.back.exception.AiException;
import org.example.back.service.impl.AiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AiServiceImpl aiService;

    @BeforeEach
    void setUp() throws Exception {
        // 设置 template 字段值，避免 NullPointerException
        Field templateField = AiServiceImpl.class.getDeclaredField("template");
        templateField.setAccessible(true);
        templateField.set(aiService, "请帮我生成一个%s任务的详细描述，任务标题是：\"%s\"\n要求：\n1. 内容具体\n2. 语言简洁\n3. 适合校园场景");

        // 设置 apiUrl 字段值
        Field apiUrlField = AiServiceImpl.class.getDeclaredField("apiUrl");
        apiUrlField.setAccessible(true);
        apiUrlField.set(aiService, "http://test.api.com");
    }

    @Test
    void generateTaskDescription_whenApiSuccess_shouldReturnDescription() {
        AiGenerateRequest request = new AiGenerateRequest();
        request.setTitle("学习任务");
        request.setType("STUDY");

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("choices", new com.alibaba.fastjson.JSONArray() {{
            add(new JSONObject() {{
                put("message", new JSONObject() {{
                    put("content", "这是一个学习任务的详细描述，包含任务要求和注意事项。");
                }});
            }});
        }});

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(JSONObject.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        AiGenerateResponse response = aiService.generateTaskDescription(request);

        assertThat(response).isNotNull();
        assertThat(response.getDescription()).isEqualTo("这是一个学习任务的详细描述，包含任务要求和注意事项。");
    }

    @Test
    void generateTaskDescription_whenApiReturnsError_shouldThrowAiException() {
        AiGenerateRequest request = new AiGenerateRequest();
        request.setTitle("测试任务");
        request.setType("LIFE");

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("error", new JSONObject() {{
            put("message", "API错误");
        }});

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(JSONObject.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        assertThatThrownBy(() -> aiService.generateTaskDescription(request))
                .isInstanceOf(AiException.class)
                .hasMessageContaining("AI连接失败");
    }

    @Test
    void generateTaskDescription_whenApiCallFails_shouldThrowAiException() {
        AiGenerateRequest request = new AiGenerateRequest();
        request.setTitle("测试任务");
        request.setType("GAME");

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(JSONObject.class)))
                .thenThrow(new RuntimeException("网络错误"));

        assertThatThrownBy(() -> aiService.generateTaskDescription(request))
                .isInstanceOf(AiException.class)
                .hasMessageContaining("AI连接失败");
    }

    @Test
    void generateTaskDescription_whenApiReturnsEmptyChoices_shouldThrowAiException() {
        AiGenerateRequest request = new AiGenerateRequest();
        request.setTitle("测试任务");
        request.setType("STUDY");

        JSONObject mockResponse = new JSONObject();
        mockResponse.put("choices", new com.alibaba.fastjson.JSONArray());

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(JSONObject.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        assertThatThrownBy(() -> aiService.generateTaskDescription(request))
                .isInstanceOf(AiException.class)
                .hasMessageContaining("AI连接失败");
    }

    @Test
    void generateTaskDescription_whenApiReturnsNull_shouldThrowAiException() {
        AiGenerateRequest request = new AiGenerateRequest();
        request.setTitle("测试任务");
        request.setType("OTHER");

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(JSONObject.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThatThrownBy(() -> aiService.generateTaskDescription(request))
                .isInstanceOf(AiException.class)
                .hasMessageContaining("AI连接失败");
    }

    @Test
    void generateTaskDescription_whenNon2xxStatus_shouldThrowAiException() {
        AiGenerateRequest request = new AiGenerateRequest();
        request.setTitle("测试任务");
        request.setType("LIFE");

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(JSONObject.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> aiService.generateTaskDescription(request))
                .isInstanceOf(AiException.class)
                .hasMessageContaining("AI连接失败");
    }
}