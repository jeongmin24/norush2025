package com.capstone.norush2025.exception;
/**
 * JWT 관련 예외
 * */
public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}
