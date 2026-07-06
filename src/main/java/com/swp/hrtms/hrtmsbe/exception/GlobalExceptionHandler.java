package com.swp.hrtms.hrtmsbe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<com.swp.hrtms.hrtmsbe.dto.response.ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(com.swp.hrtms.hrtmsbe.dto.response.ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<com.swp.hrtms.hrtmsbe.dto.response.ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(com.swp.hrtms.hrtmsbe.dto.response.ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<com.swp.hrtms.hrtmsbe.dto.response.ApiResponse<Object>> handleGlobalException(
            Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(com.swp.hrtms.hrtmsbe.dto.response.ApiResponse.error("System error: " + exception.getMessage()));
    }
}


