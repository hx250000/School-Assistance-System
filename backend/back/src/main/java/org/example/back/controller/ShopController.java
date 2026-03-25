package org.example.back.controller;

import org.example.back.common.Result;
import org.example.back.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/items")
    public Result items() {
        return Result.success(shopService.listItems());
    }

    @PostMapping("/exchange")
    public Result exchange(@RequestParam Long itemId) {
        shopService.exchange(itemId);
        return Result.success("兑换成功");
    }
}