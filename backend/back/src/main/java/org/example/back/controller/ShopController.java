package org.example.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.back.common.ApiResponse;
import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.dto.request.ShopExchangeRequest;
import org.example.back.dto.response.FileUploadResponse;
import org.example.back.entity.ShopItem;
import org.example.back.entity.ShopOrder;
import org.example.back.service.FileStorageService;
import org.example.back.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private FileStorageService fileStorageService;

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

    @Operation(summary = "上传商品图片")
    @PostMapping(value = "/items/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> uploadItemImage(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(shopService.uploadShopitemImage(file));
    }

    @GetMapping("/exchange/count")
    public ApiResponse<Long> count() {
        return ApiResponse.success(shopService.exchangeCount());
    }

    @GetMapping("/orders")
    public ApiResponse<List<ShopOrder>> listShopOrders() {
        return ApiResponse.success(shopService.listShopOrders());
    }

    @PostMapping("/orders/finish")
    public ApiResponse<ShopOrder> finishOrder(@RequestParam Long orderId){
        return ApiResponse.success(shopService.finishOrder(orderId));
    }
}