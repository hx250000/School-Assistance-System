package org.example.back.controller;

import lombok.Data;
import org.example.back.common.ApiResponse;
import org.example.back.dto.request.AiGenerateRequest;
import org.example.back.dto.response.AiGenerateResponse;
import org.example.back.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/description")
    public ApiResponse<AiGenerateResponse> generateDescription(@RequestBody AiGenerateRequest request) {
        AiGenerateResponse description = aiService.generateTaskDescription(request);
        return ApiResponse.success(description);
    }

}