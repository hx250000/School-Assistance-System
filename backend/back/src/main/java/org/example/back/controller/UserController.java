package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.request.LoginRequest;
import org.example.back.entity.User;
import org.example.back.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户模块", description = "用户注册、登录、信息获取接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "注册新用户账号")
    @PostMapping("/register")
    public ApiResponse register(@RequestBody User user) {
        return ApiResponse.success(userService.register(user));
    }

    @Operation(summary = "用户登录", description = "登录并返回 token")
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @Operation(summary = "获取当前用户信息", description = "需要登录后访问")
    @GetMapping("/info")
    public ApiResponse getUserInfo() {
        return ApiResponse.success(userService.getCurrentUser());
    }
}