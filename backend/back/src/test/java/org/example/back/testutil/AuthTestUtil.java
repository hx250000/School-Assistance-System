package org.example.back.testutil;

import org.example.back.config.JwtAuthenticationInterceptor;

import java.lang.reflect.Field;

public final class AuthTestUtil {
    private AuthTestUtil() {}

    @SuppressWarnings("unchecked")
    private static ThreadLocal<Long> currentUserIdThreadLocal() {
        try {
            Field f = JwtAuthenticationInterceptor.class.getDeclaredField("currentUserId");
            f.setAccessible(true);
            return (ThreadLocal<Long>) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("无法通过反射访问 JwtAuthenticationInterceptor.currentUserId", e);
        }
    }

    public static void setCurrentUserId(Long userId) {
        currentUserIdThreadLocal().set(userId);
    }

    public static void clear() {
        currentUserIdThreadLocal().remove();
    }
}
