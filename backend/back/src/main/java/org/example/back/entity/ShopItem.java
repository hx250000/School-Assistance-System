package org.example.back.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "shop_item")
public class ShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer price;

    private Integer stock;

    private String description;

    private String imageRes;

    @Version
    private Long version;
}