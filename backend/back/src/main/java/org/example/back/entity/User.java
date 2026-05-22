package org.example.back.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Entity
@Table(name = "users") // 数据库表名
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "用户实体")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Column(nullable = false)
    @Schema(description = "密码", example = "123456")
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "手机号", example = "13800000000")
    private String phone;

    @Column(nullable = false)
    @Schema(description = "积分", example = "100")
    private Integer points;

    @Column(nullable = false)
    @Schema(description = "信用分", example = "90")
    private Integer creditScore;

    @Column
    private String pswencp;

    @Column
    private String avatarUrl;

    @Column(nullable = false)
    @Schema(description = "等级", example = "1")
    private Integer level;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        // 新增用户时自动设置创建时间和默认值
        this.createdAt = LocalDateTime.now();
        if (this.points == null) this.points = 0;
        if (this.creditScore == null) this.creditScore = 100;
        if (this.level == null) this.level = 1;
        if (this.pswencp == null) this.pswencp = "bcrypt";
        if (this.avatarUrl == null) {
            this.avatarUrl = "uploads/avatars/default-avatar.png";
        }
    }
}