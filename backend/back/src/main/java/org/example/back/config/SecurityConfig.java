package org.example.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 将 BCrypt 注入 Spring 容器，方便你在 Service 层直接 @Autowired
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 告诉 Spring Security “放行所有请求”
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 禁用 CSRF，否则 Postman 调接口会 403
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 允许所有 HTTP 请求，不要求登录
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // 针对 H2 等控制台的保护（可选）

        return http.build();
    }
}