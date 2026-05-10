package org.example.back.service.impl;

import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.request.RegisterRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.RegisterResponse;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceConflictException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.UserRepository;
import org.example.back.service.UserService;
import org.example.back.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        checkInformation(registerRequest);
        User user = new User();
        boolean exists=userRepository.existsByUsernameOrPhone(
                registerRequest.getUsername(),
                registerRequest.getPhone());
        if(exists){
            throw new ResourceConflictException("用户名或电话号码已被使用！");
        }
        String password = encryptPassword(registerRequest.getPassword());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(password);
        user.setPhone(registerRequest.getPhone());
        // 初始化积分和信用分
        user.setPoints(0);
        user.setCreditScore(100);

        user.setPswencp("bcrypt");//BCrypt

        // 保存到数据库（自动生成ID）
        User savedUser = userRepository.save(user);

        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setUsername(savedUser.getUsername());
        registerResponse.setUserId(savedUser.getId());
        return registerResponse;
    }

    /**
     * 用户登录
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 改成根据手机号或用户名都可以登录
//        User user = userRepository.findByUsernameOrPhone(request.getPhone(), request.getUsername());
        User user=userRepository.findByPhone(request.getPhone());
        log.info("user login: " + request.getPhone());

        if (user == null ) {
            throw new AuthenticationException("用户名或密码错误!");
        }

        if (user.getPswencp()!=null&&user.getPswencp().equals("bcrypt")){
            if(!matchesPassword(request.getPassword(), user.getPassword())){
                throw new AuthenticationException("用户名或密码错误!");
            }
        }
        else{//MD5
            if(!matchesPasswordWithMd5(request.getPassword(), user.getPassword())){
                throw new AuthenticationException("用户名或密码错误!");
            }
            log.info("using old encrypt, changing to new encrypt method:");
            user.setPswencp("bcrypt");
            user.setPassword(encryptPassword(request.getPassword()));
            userRepository.save(user);
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

    public String encryptPassword(String password) {
//        return org.springframework.util.DigestUtils.md5DigestAsHex(password.getBytes());
        return passwordEncoder.encode(password);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean matchesPasswordWithMd5(String rawPassword, String encodedPassword) {
        // 使用MD5进行加密（注意：MD5不适合用于生产环境中的密码存储）
        return org.springframework.util.DigestUtils.md5DigestAsHex(rawPassword.getBytes()).equals(encodedPassword);
    }

    public void checkInformation(RegisterRequest userRegister){
        if (userRegister == null) {
            throw new ResourceConflictException("用户信息不能为空！");
        }

        log.info("user resister: "+userRegister.getPhone());

        // 1. 用户名校验
        String username = userRegister.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new ResourceConflictException("用户名不能为空！");
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new ResourceConflictException("用户名只能包含字母、数字和下划线！");
        }

        // 2. 密码校验
        String password = userRegister.getPassword();
        if (password == null || password.trim().isEmpty()) {
            throw new ResourceConflictException("密码不能为空！");
        }
        if (password.length() < 6) {
            throw new ResourceConflictException("密码长度不能少于 6 位！");
        }

        // 3. 手机号校验（中国大陆）
        String phone = userRegister.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            throw new ResourceConflictException("手机号不能为空！");
        }
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new ResourceConflictException("手机号格式不正确！");
        }

    }

}