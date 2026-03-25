package org.example.back.service;

import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.entity.User;

public interface UserService {

    Long register(User user);

    LoginResponse login(LoginRequest request);

    UserInfoVO getCurrentUser();
}