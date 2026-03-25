package org.example.back.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.back.entity.ShopItem;
import org.example.back.entity.ShopOrder;

import java.util.List;

@Mapper
public interface ShopMapper {

    List<ShopItem> listItems();

    void insertOrder(ShopOrder order);

    void updateStock(Long itemId);

    ShopItem getById(Long itemId);
}