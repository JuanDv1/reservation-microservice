package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.ReservationRepository;
import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import com.sw3.reservation_microservice.utils.ReservationTimeCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Valida que el barbero no tenga reservas que se solapen con el horario solicitado.
 */
@Component
public class BarberAvailabilityHandler extends BaseValidatorHandler {

    private static final Logger logger = LoggerFactory.getLogger(BarberAvailabilityHandler.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        String barberId = request.getBarberId();
        
        if (barberId == null || barberId.trim().isEmpty()) {
            throw new ReservationValidationException("El ID del barbero es obligatorio.");
        }

        // Obtener el servicio para calcular el endTime
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new ReservationValidationException("Servicio no encontrado."));

        // Calcular el endTime basado en la duración del servicio
        LocalDateTime endTime = ReservationTimeCalculator.calculateEndTime(
            request.getStartTime(),
            service.getDuration()
        );

        // Buscar reservas que se solapen
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
            barberId,
            request.getStartTime(),
            endTime
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
