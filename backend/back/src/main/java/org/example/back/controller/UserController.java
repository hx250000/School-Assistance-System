package org.example.back.controller;

import org.example.back.common.Result;
import org.example.back.dto.request.LoginRequest;
import org.example.back.entity.User;
import org.example.back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return Result.success(userService.register(user));
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        return Result.success(userService.getCurrentUser());
    }
}