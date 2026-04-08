package org.example.back.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.back.util.JwtUtil;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                Long userId = JwtUtil.getUserIdFromToken(token);
                currentUserId.set(userId);
            } catch (Exception e) {
                // Token invalid, but we don't block the request here
                // Service layer will handle authentication as needed
                currentUserId.remove();
            }
        } else {
            currentUserId.remove();
        }

        return true; // Continue with the request
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