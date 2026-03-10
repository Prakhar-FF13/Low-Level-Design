package com.springmicroservice.lowleveldesignproblems.cachesystem.domain.exception;

public class CacheFullException extends RuntimeException {
  public CacheFullException(String message) {
    super(message);
  }
}
