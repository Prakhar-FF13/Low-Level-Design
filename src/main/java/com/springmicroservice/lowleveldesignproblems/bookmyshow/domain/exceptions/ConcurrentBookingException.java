package com.springmicroservice.lowleveldesignproblems.bookmyshow.domain.exceptions;

public class ConcurrentBookingException extends BookingException {

    public ConcurrentBookingException(String message) {
        super(message);
    }

    public ConcurrentBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
