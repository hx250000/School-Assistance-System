package org.example.back.service;

import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.request.RegisterRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.entity.User;

public interface UserService {

    Long register(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest request);

    UserInfoVO getCurrentUser();
}