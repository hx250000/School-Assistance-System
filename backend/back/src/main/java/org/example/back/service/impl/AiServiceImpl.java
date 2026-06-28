package org.example.back.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.back.dto.request.AiGenerateRequest;
import org.example.back.dto.response.AiGenerateResponse;
import org.example.back.exception.AiException;
import org.example.back.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class AiServiceImpl implements AiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-secret}")
    private String apiSecret;

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.model}")
    private String model;

    @Value("${ai.template}")
    private String template;

    /*private String template="""
            请帮我生成一个%s任务的详细描述，任务标题是："%s"
            要求：
            1. 内容具体，包含任务要求和注意事项
            2. 语言简洁，适合校园场景
            3. 字数控制在80字左右
            """;*/

    @Override
    public AiGenerateResponse generateTaskDescription(AiGenerateRequest aiGenerateRequest) {
        String taskType=aiGenerateRequest.getType();
        String taskTitle=aiGenerateRequest.getTitle();
        if(taskTitle==null||taskTitle.isBlank()){
            throw new IllegalArgumentException("请输入任务标题");
        }
        // 构建 Prompt
        String prompt = buildPrompt(taskTitle, taskType);
        // 调用 LLM API
        String llmResponse = callLlmApi(prompt);
        // 解析并返回结果
        String desc=parseResponse(llmResponse);
        if(desc==null){
            throw new AiException("AI连接失败，请手动输入");
        }
        AiGenerateResponse response=new AiGenerateResponse(desc);
        return response;
    }

    private String buildPrompt(String title, String type) {
        String typeName = switch(type) {
            case "LIFE" -> "生活类";
            case "GAME" -> "游戏类";
            case "STUDY" -> "学习类";
            default -> "其他";
        };

        return String.format(template, typeName, title);
    }

    /**
     * 调用讯飞星火 Spark X2 API
     */
    private String callLlmApi(String prompt) {
        // 构建请求体（标准 OpenAI 格式）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 200);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 讯飞星火OpenAI兼容接口的鉴权格式：Bearer APIKey:APISecret
        String token = String.format("Bearer %s:%s", apiKey, apiSecret);
        headers.set("Authorization", token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<JSONObject> response = restTemplate.postForEntity(apiUrl, request, JSONObject.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("LLM API 调用成功");
                return response.getBody().toJSONString();
            } else {
                log.error("LLM API 调用失败，状态码: {}, 响应体: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("LLM API 调用异常", e);

        }

        return null;
    }

    /**
     * 解析讯飞星火 API 响应
     */
    private String parseResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            log.warn("LLM API 响应为空");
            return null;
        }

        try {
            JSONObject json = JSON.parseObject(rawResponse);

            // 检查是否有错误
            if (json.containsKey("error")) {
                JSONObject error = json.getJSONObject("error");
                log.error("LLM API 返回错误: {}", error.getString("message"));
                return null;
            }

            // 解析响应（讯飞星火响应格式与 OpenAI 兼容）
            JSONArray choices = json.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                if (message != null) {
                    String content = message.getString("content");
                    return content != null ? content.trim() : null;
                }
            }
        } catch (Exception e) {
            log.error("LLM API 响应解析失败", e);
        }

        return null;
    }
}
