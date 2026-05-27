package org.example.back.dto.request;

import lombok.Data;

@Data
public class AiGenerateRequest {
    private String title;
    private String type; // 任务类型（LIFE/GAME/STUDY）
}
