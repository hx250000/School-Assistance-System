package org.example.back.service;

import org.example.back.dto.request.LoginRequest;
import org.example.back.dto.request.RegisterRequest;
import org.example.back.dto.response.LoginResponse;
import org.example.back.dto.response.RegisterResponse;
import org.example.back.dto.response.UserInfoVO;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
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
import org.mockito.junit.jupiter.MockitoExtension;

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

    @AfterEach
    void tearDown() {
        AuthTestUtil.clear();
    }

    @Test
    void register_shouldInitPointsAndCreditScore_andReturnRegisterResponse() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("u1");
        req.setPhone("13000000000");
        req.setPassword("p1");

        User saved = new User();
        saved.setId(10L);
        saved.setUsername("u1");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        RegisterResponse resp = userService.register(req);

        assertThat(resp.getUserId()).isEqualTo(10L);
        assertThat(resp.getUsername()).isEqualTo("u1");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();
        assertThat(toSave.getUsername()).isEqualTo("u1");
        assertThat(toSave.getPhone()).isEqualTo("13000000000");
        assertThat(toSave.getPassword()).isEqualTo("p1");
        assertThat(toSave.getPoints()).isEqualTo(0);
        assertThat(toSave.getCreditScore()).isEqualTo(100);
    }

    @Test
    void login_whenUserNotFound_shouldThrowAuthenticationException() {
        LoginRequest req = new LoginRequest();
        req.setPhone("13000000000");
        req.setPassword("wrong");

        when(userRepository.findByPhone("13000000000")).thenReturn(null);

        assertThatThrownBy(() -> userService.login(req))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    void login_whenPasswordMismatch_shouldThrowAuthenticationException() {
        LoginRequest req = new LoginRequest();
        req.setPhone("13000000000");
        req.setPassword("p2");

        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setPassword("p1");
        when(userRepository.findByPhone("13000000000")).thenReturn(user);

        assertThatThrownBy(() -> userService.login(req))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    void login_whenSuccess_shouldReturnTokenAndUserInfo() {
        LoginRequest req = new LoginRequest();
        req.setPhone("13000000000");
        req.setPassword("p1");

        User user = new User();
        user.setId(1L);
        user.setUsername("u1");
        user.setPassword("p1");
        when(userRepository.findByPhone("13000000000")).thenReturn(user);

        LoginResponse resp = userService.login(req);

        assertThat(resp.getUserId()).isEqualTo(1L);
        assertThat(resp.getUsername()).isEqualTo("u1");
        assertThat(resp.getToken()).isNotBlank();
    }

    @Test
    void getCurrentUser_whenNotLoggedIn_shouldThrowAuthenticationException() {
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

