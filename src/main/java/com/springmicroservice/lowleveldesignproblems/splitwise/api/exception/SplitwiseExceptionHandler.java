package com.springmicroservice.lowleveldesignproblems.splitwise.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.springmicroservice.lowleveldesignproblems.splitwise.exception.SplitwiseException;

@RestControllerAdvice(basePackages = "com.springmicroservice.lowleveldesignproblems.splitwise.api.controller")
public class SplitwiseExceptionHandler {

    @ExceptionHandler(SplitwiseException.class)
    public ResponseEntity<String> splitwise(SplitwiseException ex) {
        HttpStatus status = ex.isNotFound() ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validation(MethodArgumentNotValidException ex) {
        String msg =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(f -> f.getField() + ": " + f.getDefaultMessage())
                        .findFirst()
                        .orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
}
