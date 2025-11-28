package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.controller.dto.RescheduleReservationRequestDTO;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.service.validation.RescheduleValidatorHandler;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Valida el horario de negocio para reprogramación.
 */
@Component
public class BusinessHoursForRescheduleHandler extends RescheduleValidatorHandler {

    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);

    @Override
    protected void validateConcrete(RescheduleReservationRequestDTO request, String barberId, Long reservationId) {
        LocalTime startTime = request.getStartTime().toLocalTime();
        LocalTime endTime = request.getEndTime().toLocalTime();
        DayOfWeek dayOfWeek = request.getStartTime().getDayOfWeek();

        // No se trabaja los domingos
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            throw new ReservationValidationException(
                "No se puede reprogramar para domingos. La barbería está cerrada."
            );
        }

        // Validar horario de apertura
        if (startTime.isBefore(OPENING_TIME)) {
            throw new ReservationValidationException(
                "No se puede reprogramar antes de las 8:00 AM."
            );
        }

        // Validar horario de cierre
        if (endTime.isAfter(CLOSING_TIME)) {
            throw new ReservationValidationException(
                "No se puede reprogramar después de las 6:00 PM."
            );
        }
    }
}
