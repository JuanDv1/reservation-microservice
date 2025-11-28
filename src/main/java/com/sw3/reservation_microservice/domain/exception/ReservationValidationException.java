package com.sw3.reservation_microservice.domain.exception;

public class ReservationValidationException extends RuntimeException {
    
    public ReservationValidationException(String message) {
        super(message);
    }
    
    public ReservationValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
