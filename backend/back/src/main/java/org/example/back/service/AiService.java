package org.example.back.service;

import org.example.back.dto.request.AiGenerateRequest;
import org.example.back.dto.response.AiGenerateResponse;

public interface AiService {
    AiGenerateResponse generateTaskDescription(AiGenerateRequest aiGenerateRequest);
}
