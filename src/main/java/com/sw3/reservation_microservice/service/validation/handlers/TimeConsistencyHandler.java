package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Valida la consistencia de las fechas de la reserva.
 */
@Component
public class TimeConsistencyHandler extends BaseValidatorHandler {

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();
        LocalDateTime now = LocalDateTime.now();

        // Validar que las fechas no sean nulas
        if (start == null || end == null) {
            throw new ReservationValidationException("Las fechas de inicio y fin son obligatorias.");
        }

        // Validar que la fecha de inicio sea futura
        if (start.isBefore(now)) {
            throw new ReservationValidationException("La fecha de inicio debe ser futura.");
        }

        // Validar que la fecha de fin sea posterior a la de inicio
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new ReservationValidationException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        // Validar duración mínima (ej: 15 minutos)
        long minutesDuration = java.time.Duration.between(start, end).toMinutes();
        if (minutesDuration < 15) {
            throw new ReservationValidationException("La reserva debe tener una duración mínima de 15 minutos.");
        }
    }
}
