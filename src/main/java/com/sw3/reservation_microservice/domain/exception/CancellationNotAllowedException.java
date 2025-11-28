package com.sw3.reservation_microservice.domain.exception;

public class CancellationNotAllowedException extends RuntimeException {
    
    public CancellationNotAllowedException(String message) {
        super(message);
    }
    
    public CancellationNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
