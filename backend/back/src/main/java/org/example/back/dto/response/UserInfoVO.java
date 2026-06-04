package org.example.back.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String phone;
    private String avatarUrl;
    private Integer points;
    private Integer creditScore;
    private Integer level;
    private Boolean admin;
    private LocalDateTime createdAt;   // 可用 String 或 LocalDateTime，返回 JSON 时 Jackson 会自动序列化
}