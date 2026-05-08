package org.example.back.dto.request;

import lombok.Data;

@Data
public class NewShopItemRequest {
    private String name;
    private Integer price;
    private Integer stock;

    private String description;
    private String imageRes;
}
