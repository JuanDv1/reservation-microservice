package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Valida que la reserva esté dentro del horario de operación de la barbería.
 */
@Component
public class BusinessHoursHandler extends BaseValidatorHandler {

    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);   // 8:00 AM
    private static final LocalTime CLOSING_TIME = LocalTime.of(20, 0);  // 8:00 PM

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();

        // Validar que no sea domingo (día de descanso)
        if (start.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new ReservationValidationException("La barbería no opera los domingos.");
        }

        // Validar horario de inicio
        LocalTime startTime = start.toLocalTime();
        if (startTime.isBefore(OPENING_TIME)) {
            throw new ReservationValidationException(
                "El horario de inicio debe ser después de las " + OPENING_TIME
            );
        }

        // Validar horario de fin
        LocalTime endTime = end.toLocalTime();
        if (endTime.isAfter(CLOSING_TIME)) {
            throw new ReservationValidationException(
                "El horario de fin debe ser antes de las " + CLOSING_TIME
            );
        }
    }
}
