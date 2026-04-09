package org.example.back.service.impl;

import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.request.RegisterRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.UserRepository;
import org.example.back.service.UserService;
import org.example.back.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户注册
     */
    @Override
    @Transactional
    public Long register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setPhone(registerRequest.getPhone());
        // 初始化积分和信用分
        user.setPoints(0);
        user.setCreditScore(100);

        // 保存到数据库（自动生成ID）
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    /**
     * 用户登录
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 改成根据手机号或用户名都可以登录
        User user = userRepository.findByUsernameOrPhone(request.getUsername(), request.getUsername());

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new AuthenticationException("用户名或密码错误");
        }

        // 生成JWT
        String token = JwtUtil.generateToken(user.getId());

        // 构造返回对象
        LoginResponse res = new LoginResponse();
        res.setToken(token);
        res.setUserId(user.getId());
        res.setUsername(user.getUsername());

        return res;
    }

    /**
     * 获取当前用户信息
     */
    @Override
    public UserInfoVO getCurrentUser() {
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();

        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户"+userId+"不存在"));

        UserInfoVO vo = new UserInfoVO();
        // 复制属性
        BeanUtils.copyProperties(user, vo);

        return vo;
    }

    @Override
    public List<UserInfoVO> getAllUsersInfo() {
        List<User> users = userRepository.findAll();
        List<UserInfoVO> vos = new ArrayList<>();
        for (User user : users) {
            UserInfoVO vo = new UserInfoVO();
            BeanUtils.copyProperties(user, vo);
            vos.add(vo);
        }
        return vos;
    }
}