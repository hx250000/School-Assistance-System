package org.example.back.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    public static final String SECRET = "yourverylongsecretkeyshouldbeatleast32bytes";
}