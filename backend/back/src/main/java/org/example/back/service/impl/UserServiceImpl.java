package org.example.back.service.impl;

import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.entity.User;
import org.example.back.mapper.UserMapper;
import org.example.back.service.UserService;
import org.example.back.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long register(User user) {
        user.setPoints(0);
        user.setCreditScore(100);
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userMapper.findByUsername(request.getUsername());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId());

        LoginResponse res = new LoginResponse();
        res.setToken(token);
        res.setUserId(user.getId());
        res.setUsername(user.getUsername());

        return res;
    }

    @Override
    public UserInfoVO getCurrentUser() {

        Long userId = 1L; // TODO 从JWT解析

        User user = userMapper.selectById(userId);

        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);

        return vo;
    }
}