package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.springframework.stereotype.Component;

/**
 * Valida que los campos obligatorios estén presentes.
 */
@Component
public class RequiredFieldsHandler extends BaseValidatorHandler {

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        if (request.getClientId() == null || request.getClientId().trim().isEmpty()) {
            throw new ReservationValidationException("El ID del cliente es obligatorio.");
        }

        if (request.getBarberId() == null || request.getBarberId().trim().isEmpty()) {
            throw new ReservationValidationException("El ID del barbero es obligatorio.");
        }

        if (request.getServiceId() == null) {
            throw new ReservationValidationException("El ID del servicio es obligatorio.");
        }

        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new ReservationValidationException("El precio debe ser mayor a 0.");
        }
    }
}
