package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.request.RegisterRequest;
import org.example.back.dto.response.FileUploadResponse;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.RegisterResponse;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户模块", description = "用户注册、登录、信息获取接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "注册新用户账号")
    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ApiResponse.success(userService.register(registerRequest));
    }

    @Operation(summary = "用户登录", description = "登录并返回 token")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @Operation(summary = "获取当前用户信息", description = "需要登录后访问")
    @GetMapping("/info")
    public ApiResponse<UserInfoVO> getUserInfo() {
        return ApiResponse.success(userService.getCurrentUser());
    }

    @Operation(summary = "获取所有用户", description = "返回所有用户列表")
    @GetMapping("/all")
    public ApiResponse<List<UserInfoVO>> getAllUsersInfo() {
        return ApiResponse.success(userService.getAllUsersInfo());
    }

    @Operation(summary = "上传用户头像")
    @PostMapping(value = "/info/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> uploadAvatar(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(userService.uploadAvatar(file));
    }

    @Operation(summary = "管理登录", description = "登录并返回 token")
    @PostMapping("/admin/login")
    public ApiResponse<LoginResponse> adminLogin(@RequestBody LoginRequest request) {
        return ApiResponse.success(userService.adminLogin(request));
    }
}