package org.example.back.dto.response;

import lombok.Data;

@Data
public class AvatarUploadResponse {
    private long userId;
    private String avatarUrl;
}
