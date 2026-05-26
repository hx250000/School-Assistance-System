package org.example.back.dto.response;

import lombok.Data;

@Data
public class AiGenerateResponse {
    private String description;
    public AiGenerateResponse(String description) {
        this.description = description;
    }
}
