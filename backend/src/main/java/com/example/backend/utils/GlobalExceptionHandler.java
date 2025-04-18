package com.example.backend.utils;

import jakarta.validation.ConstraintViolationException;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleException(BadRequestException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Map<String, String>> handleException(InternalServerException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());

        if (ex.getCause() != null) {
            response.put("cause", ex.getCause().getMessage());
        }

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }
}
