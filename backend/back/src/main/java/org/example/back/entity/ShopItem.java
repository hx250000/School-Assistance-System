package org.example.back.entity;
import lombok.Data;
@Data
public class ShopItem {
    private Long id;
    private String name;
    private Integer price;
    private Integer stock;

    private String description;
}