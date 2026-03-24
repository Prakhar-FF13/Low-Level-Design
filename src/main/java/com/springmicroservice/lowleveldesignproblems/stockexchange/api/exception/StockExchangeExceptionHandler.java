package com.springmicroservice.lowleveldesignproblems.stockexchange.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions.OrderNotFoundException;
import com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions.TradingException;
import com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions.UnauthorizedOrderAccessException;

@RestControllerAdvice(basePackages = "com.springmicroservice.lowleveldesignproblems.stockexchange.api.controller")
public class StockExchangeExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedOrderAccessException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedOrderAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(TradingException.class)
    public ResponseEntity<String> handleTrading(TradingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
