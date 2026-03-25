package org.example.back.entity;

import java.time.LocalDateTime;
import lombok.Data;
@Data
public class ShopOrder {
    private Long id;
    private Long userId;
    private Long itemId;

    private String status; // PAID / FINISHED

    private LocalDateTime createdAt;
}