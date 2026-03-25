package org.example.back.entity;

import java.time.LocalDateTime;
import lombok.Data;
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;

    private Integer points;        // 积分
    private Integer creditScore;   // 信用分
    private Integer level;         // 等级

    private LocalDateTime createdAt;
}