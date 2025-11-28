package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.ReservationRepository;
import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Valida que el barbero no tenga reservas que se solapen con el horario solicitado.
 */
@Component
public class BarberAvailabilityHandler extends BaseValidatorHandler {

    private static final Logger logger = LoggerFactory.getLogger(BarberAvailabilityHandler.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        String barberId = request.getBarberId();
        
        if (barberId == null || barberId.trim().isEmpty()) {
            throw new ReservationValidationException("El ID del barbero es obligatorio.");
        }

        // Buscar reservas que se solapen
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
            barberId,
            request.getStartTime(),
            request.getEndTime()
        );

        logger.info("Barbero: {}, Reservas solapadas encontradas: {}", barberId, overlappingReservations.size());
        
        if (!overlappingReservations.isEmpty()) {
            logger.warn("Reserva rechazada - Barbero {} ya tiene {} reserva(s) en ese horario", 
                        barberId, overlappingReservations.size());
            throw new ReservationValidationException(
                "El barbero no está disponible en el horario solicitado. Ya tiene una reserva."
            );
        }
    }
}
