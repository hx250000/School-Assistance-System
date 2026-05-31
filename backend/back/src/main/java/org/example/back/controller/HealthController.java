package org.example.back.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "健康检查", description = "服务状态监控接口") // 加这个
public class HealthController {

    @Operation(summary = "获取服务健康状态") // 加这个
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "healthy");
        res.put("timestamp", LocalDateTime.now().toString());
        res.put("version", "1.0.0");
        return res;
    }
}