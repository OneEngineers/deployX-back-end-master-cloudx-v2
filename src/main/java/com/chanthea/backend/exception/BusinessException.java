package com.chanthea.backend.exception;

// This is a custom class so you can throw specific business errors
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}