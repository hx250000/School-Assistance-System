package org.example.back.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.back.exception.AuthenticationException;
import org.example.back.util.JwtUtil;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthenticationException("未认证或缺少 Authorization header");
        }

        String token = authHeader.substring(7);
        try {
            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new AuthenticationException("Token 无效");
            }
            currentUserId.set(userId);
            return true;
        } catch (Exception e) {
            currentUserId.remove();
            throw new AuthenticationException("Token 无效或已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Clean up ThreadLocal after request completion
        currentUserId.remove();
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }
}