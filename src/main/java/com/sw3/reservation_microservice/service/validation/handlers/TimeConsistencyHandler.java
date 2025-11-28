package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
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
        LocalDateTime now = LocalDateTime.now();

        // Validar que la fecha de inicio no sea nula
        if (start == null) {
            throw new ReservationValidationException("La fecha de inicio es obligatoria.");
        }

        // Validar que la fecha de inicio sea futura
        if (start.isBefore(now)) {
            throw new ReservationValidationException("La fecha de inicio debe ser futura.");
        }

        // Nota: El endTime se calcula automáticamente en el servicio basado en la duración del servicio
        // No es necesario validar duración mínima aquí ya que viene de la configuración del servicio
    }
}
