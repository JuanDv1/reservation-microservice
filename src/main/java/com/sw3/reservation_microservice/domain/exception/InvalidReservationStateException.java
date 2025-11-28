package com.sw3.reservation_microservice.domain.exception;

public class InvalidReservationStateException extends RuntimeException {
    
    public InvalidReservationStateException(String message) {
        super(message);
    }
    
    public InvalidReservationStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
