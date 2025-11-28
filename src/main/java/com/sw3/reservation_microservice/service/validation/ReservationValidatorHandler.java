package com.sw3.reservation_microservice.service.validation;
import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;

public interface ReservationValidatorHandler {
    //Metodo para establecer el siguiente validador en la cadena
    void setNext(ReservationValidatorHandler next);

    //Metodo que ejecuta la logica de validacion
    void validate(CreateReservationRequestDTO request);
}
