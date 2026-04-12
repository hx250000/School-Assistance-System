package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/items")
    public ApiResponse items() {
        return ApiResponse.success(shopService.listItems());
    }

    @PostMapping("/exchange")
    public ApiResponse exchange(@RequestParam Long itemId) {
        return ApiResponse.success(shopService.exchange(itemId));
    }

    @PostMapping("/items")
    public ApiResponse addItem(@RequestBody NewShopItemRequest request) {
        return ApiResponse.success(shopService.addItem(request));
    }
}