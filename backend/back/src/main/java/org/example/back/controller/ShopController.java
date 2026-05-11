package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.dto.request.ShopExchangeRequest;
import org.example.back.entity.ShopItem;
import org.example.back.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/items")
    public ApiResponse<List<ShopItem>> items() {
        return ApiResponse.success(shopService.listItems());
    }

    @PostMapping("/exchange")
    public ApiResponse<Long> exchange(@RequestBody ShopExchangeRequest req) {
        return ApiResponse.success(shopService.exchange(req.getItemId()));
    }

    @PostMapping("/items")
    public ApiResponse<Long> addItem(@RequestBody NewShopItemRequest request) {
        return ApiResponse.success(shopService.addItem(request));
    }

    @GetMapping("/exchange/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success(shopService.exchangeCount());
    }
}