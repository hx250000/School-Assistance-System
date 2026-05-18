package org.example.back.service;

import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.entity.ShopItem;

import java.util.List;

public interface ShopService {

    List<ShopItem> listItems();

    Long exchange(Long itemId);

    Long addItem(NewShopItemRequest request);

    Long exchangeCount();
}