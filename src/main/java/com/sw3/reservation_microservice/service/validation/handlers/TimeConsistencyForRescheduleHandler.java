package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.RescheduleReservationRequestDTO;
import com.sw3.reservation_microservice.service.validation.RescheduleValidatorHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Valida la consistencia de tiempo para reprogramación.
 */
@Component
public class TimeConsistencyForRescheduleHandler extends RescheduleValidatorHandler {

    @Override
    protected void validateConcrete(RescheduleReservationRequestDTO request, String barberId, Long reservationId) {
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        // Validar que endTime sea después de startTime
        if (!endTime.isAfter(startTime)) {
            throw new ReservationValidationException(
                "La hora de finalización debe ser posterior a la hora de inicio."
            );
        }

        // Validar que sea una fecha futura
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new ReservationValidationException(
                "No se puede reprogramar una reserva para una fecha pasada."
            );
        }
    }
}
