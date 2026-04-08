package org.example.back.exception;

public class ResourceConflictException extends RuntimeException {
    /**
     * 用于处理业务逻辑冲突异常，如容量已满、重复选课等。
     */

    public ResourceConflictException(String message) {
        super(message);
    }

}
