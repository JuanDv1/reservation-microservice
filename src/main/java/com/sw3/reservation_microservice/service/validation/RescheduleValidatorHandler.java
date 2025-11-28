package com.sw3.reservation_microservice.service.validation;

import com.sw3.reservation_microservice.controller.dto.RescheduleReservationRequestDTO;

/**
 * Interface base para los validadores de reprogramación.
 */
public abstract class RescheduleValidatorHandler {

    protected RescheduleValidatorHandler next;

    public void setNext(RescheduleValidatorHandler next) {
        this.next = next;
    }

    public void validate(RescheduleReservationRequestDTO request, String barberId, Long reservationId) {
        validateConcrete(request, barberId, reservationId);
        if (next != null) {
            next.validate(request, barberId, reservationId);
        }
    }

    protected abstract void validateConcrete(RescheduleReservationRequestDTO request, String barberId, Long reservationId);
}
