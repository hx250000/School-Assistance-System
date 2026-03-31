package org.example.back.service.impl;

import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.entity.User;
import org.example.back.repository.UserRepository;
import org.example.back.service.UserService;
import org.example.back.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户注册
     */
    @Override
    @Transactional
    public Long register(User user) {
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
            throw new RuntimeException("用户名或密码错误");
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
        Long userId = 1L; // TODO: 从JWT解析获取实际用户ID

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserInfoVO vo = new UserInfoVO();
        // 复制属性
        BeanUtils.copyProperties(user, vo);

        return vo;
    }
}