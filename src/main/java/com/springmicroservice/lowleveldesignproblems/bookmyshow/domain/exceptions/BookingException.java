package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.exceptions;

public class BookingException extends RuntimeException {

    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
