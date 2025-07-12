package com.capstone.norush2025.exception;
/**
 * 권한이 없는 사용자 접근시 예외
 * */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
