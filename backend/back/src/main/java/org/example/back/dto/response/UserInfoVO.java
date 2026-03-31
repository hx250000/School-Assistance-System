package org.example.back.dto.response;

import lombok.Data;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String phone;       // 原来是 email，现在改成 phone
    private Integer points;
    private Integer creditScore;
    private Integer level;
    private String createdAt;   // 可用 String 或 LocalDateTime，返回 JSON 时 Jackson 会自动序列化
}