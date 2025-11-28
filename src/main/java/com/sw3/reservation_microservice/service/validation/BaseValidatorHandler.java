package com.sw3.reservation_microservice.service.validation;

import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;

public abstract class BaseValidatorHandler implements ReservationValidatorHandler {
    private ReservationValidatorHandler next;

    @Override
    public void setNext(ReservationValidatorHandler next) {
        this.next = next;
    }

    @Override
    public void validate(CreateReservationRequestDTO request) {
        //Logica de validacion especifica del validador actual
        validateConcrete(request);
        //Si pasa la validacion, se llama al siguiente validador en la cadena
        if (next != null) {
            next.validate(request);
        }
    }

    protected abstract void validateConcrete(CreateReservationRequestDTO request);
}