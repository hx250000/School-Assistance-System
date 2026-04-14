package org.example.back.service;

import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.request.RegisterRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.RegisterResponse;
import org.example.back.dto.response.UserInfoVO;

import java.util.List;

public interface UserService {

    RegisterResponse register(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest request);

    UserInfoVO getCurrentUser();

    List<UserInfoVO> getAllUsersInfo();
}