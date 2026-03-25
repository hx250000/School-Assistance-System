package org.example.back.service;

import org.example.back.entity.ShopItem;

import java.util.List;

public interface ShopService {

    List<ShopItem> listItems();

    void exchange(Long itemId);
}