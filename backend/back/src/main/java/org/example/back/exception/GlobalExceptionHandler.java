package org.example.back.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.back.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 * 统一处理API异常，返回标准格式的错误响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.info("ResourceNotFoundException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.notFound(ex.getMessage()));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceConflict(ResourceConflictException ex) {
        log.info("ResourceConflictException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.error(409,ex.getMessage()));
    }

//    /**
//     * 处理Spring MVC的资源未找到异常
//     */
//    @ExceptionHandler(NoResourceFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex) {
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(ApiResponse.notFound("Resource not found: " + ex.getResourcePath()));
//    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.badRequest(ex.getMessage()));
    }

    /** 
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthenticationException ex) {
        log.info("Authentication exception"+ ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.error(401, ex.getMessage()));
    }
//    /**
//     * 处理资源冲突异常
//     */
//    @ExceptionHandler(ResourceConflictException.class)
//    public ResponseEntity<ApiResponse<Void>> handleBusinessException(ResourceConflictException ex) {
//        return ResponseEntity
//                .status(HttpStatus.CONFLICT)
//                .body(ApiResponse.error(409, ex.getMessage()));
//    }


    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.info("Exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.error(500, "服务器异常，请稍后再试" ));
    }
}
