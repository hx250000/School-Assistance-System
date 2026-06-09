package org.example.back.service;

import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.dto.response.FileUploadResponse;
import org.example.back.entity.ShopItem;
import org.example.back.entity.ShopOrder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopService {

    List<ShopItem> listItems();

    Long exchange(Long itemId);

    Long addItem(NewShopItemRequest request);

    Long exchangeCount();

    List<ShopOrder> listShopOrders();

    @Transactional
    ShopOrder finishOrder(Long orderId);

    FileUploadResponse uploadShopitemImage(MultipartFile file);
}