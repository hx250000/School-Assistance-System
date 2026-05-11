package org.example.back.service;

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
import org.example.back.service.impl.UserServiceImpl;
import org.example.back.testutil.AuthTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Spy
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @AfterEach
    void tearDown() {
        AuthTestUtil.clear();
    }

    // 辅助方法：模拟业务代码中的加密逻辑
    private String md5(String psd) {
        return DigestUtils.md5DigestAsHex(psd.getBytes());
    }

    @Test
    void register_shouldInitPointsAndCreditScore_andReturnRegisterResponse() {
        // 1. 准备请求数据（需符合校验规则：用户名英文数字，密码>=6位，手机号正则）
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user123");
        req.setPhone("13812345678");
        req.setPassword("password123");

        // 2. Mock 查重逻辑
        when(userRepository.existsByUsernameOrPhone(req.getUsername(), req.getPhone())).thenReturn(false);

        User saved = new User();
        saved.setId(10L);
        saved.setUsername("user123");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        // 3. 执行
        RegisterResponse resp = userService.register(req);

        // 4. 断言返回结果
        assertThat(resp.getUserId()).isEqualTo(10L);
        assertThat(resp.getUsername()).isEqualTo("user123");

        // 5. 验证保存的对象属性
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();

        assertThat(toSave.getUsername()).isEqualTo("user123");
        assertThat(toSave.getPhone()).isEqualTo("13812345678");
        assertThat(passwordEncoder.matches("password123", toSave.getPassword())).isTrue();
        assertThat(toSave.getPoints()).isEqualTo(0);
        assertThat(toSave.getCreditScore()).isEqualTo(100);
        assertThat(toSave.getPswencp()).isEqualTo("bcrypt");
    }

    @Test
    void register_whenUserExists_shouldThrowConflictException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("exists");
        req.setPhone("13812345678");
        req.setPassword("password123");

        when(userRepository.existsByUsernameOrPhone(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("用户名或电话号码已被使用");
    }

    @Test
    void register_withInvalidPhone_shouldThrowException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPhone("123"); // 错误格式
        req.setPassword("password123");

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("手机号格式不正确");
    }

    @Test
    void register_withEmptyUsername_shouldThrowException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("");
        req.setPhone("13812345678");
        req.setPassword("password123");

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("用户名不能为空");
    }

    @Test
    void register_withInvalidUsername_shouldThrowException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user@name"); // 包含非法字符
        req.setPhone("13812345678");
        req.setPassword("password123");

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("用户名只能包含字母、数字和下划线");
    }

    @Test
    void register_withShortPassword_shouldThrowException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user123");
        req.setPhone("13812345678");
        req.setPassword("123"); // 太短

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("密码长度不能少于 6 位");
    }

    @Test
    void login_withMd5Password_shouldConvertToBcrypt() {
        LoginRequest req = new LoginRequest();
        req.setPhone("13800000000");
        req.setPassword("p123456");

        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setPassword(DigestUtils.md5DigestAsHex("p123456".getBytes()));
        user.setPswencp("md5");
        when(userRepository.findByPhone("13800000000")).thenReturn(user);

        LoginResponse resp = userService.login(req);

        assertThat(resp.getUserId()).isEqualTo(1L);
        verify(userRepository).save(argThat(u -> "bcrypt".equals(u.getPswencp())));
    }

    @Test
    void login_whenUserNotFound_shouldThrowAuthenticationException() {
        LoginRequest req = new LoginRequest();
        req.setPhone("13800000000");
        req.setPassword("any_pass");

        when(userRepository.findByPhone("13800000000")).thenReturn(null);

        assertThatThrownBy(() -> userService.login(req))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    void login_whenPasswordMismatch_shouldThrowAuthenticationException() {
        LoginRequest req = new LoginRequest();
        req.setPhone("13800000000");
        req.setPassword("wrong_password");

        User user = new User();
        user.setPassword(passwordEncoder.encode("correct_password"));
        user.setPswencp("bcrypt");
        when(userRepository.findByPhone("13800000000")).thenReturn(user);

        assertThatThrownBy(() -> userService.login(req))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    void login_whenSuccess_shouldReturnTokenAndUserInfo() {
        LoginRequest req = new LoginRequest();
        req.setPhone("13800000000");
        req.setPassword("p123456");

        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setPassword(passwordEncoder.encode("p123456"));
        user.setPswencp("bcrypt");
        when(userRepository.findByPhone("13800000000")).thenReturn(user);

        LoginResponse resp = userService.login(req);

        assertThat(resp.getUserId()).isEqualTo(1L);
        assertThat(resp.getUsername()).isEqualTo("u1");
        assertThat(resp.getToken()).isNotBlank();
    }

    @Test
    void getCurrentUser_whenNotLoggedIn_shouldThrowAuthenticationException() {
        // 模拟拦截器没有存入 ID 的情况
        AuthTestUtil.clear();

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void getCurrentUser_whenNotFound_shouldThrowResourceNotFoundException() {
        AuthTestUtil.setCurrentUserId(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("用户99不存在");
    }

    @Test
    void getCurrentUser_whenSuccess_shouldCopyPropertiesToVO() {
        AuthTestUtil.setCurrentUserId(2L);
        User u = new User();
        u.setId(2L);
        u.setUsername("u2");
        u.setPhone("13100000000");
        u.setPoints(12);
        u.setCreditScore(88);
        when(userRepository.findById(2L)).thenReturn(Optional.of(u));

        UserInfoVO vo = userService.getCurrentUser();

        assertThat(vo.getId()).isEqualTo(2L);
        assertThat(vo.getUsername()).isEqualTo("u2");
        assertThat(vo.getPhone()).isEqualTo("13100000000");
        assertThat(vo.getPoints()).isEqualTo(12);
        assertThat(vo.getCreditScore()).isEqualTo(88);
    }

    @Test
    void getAllUsersInfo_shouldMapAllUsersToVOList() {
        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("a");
        User u2 = new User();
        u2.setId(2L);
        u2.setUsername("b");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserInfoVO> vos = userService.getAllUsersInfo();

        assertThat(vos).hasSize(2);
        assertThat(vos.get(0).getId()).isEqualTo(1L);
        assertThat(vos.get(1).getId()).isEqualTo(2L);
    }
}