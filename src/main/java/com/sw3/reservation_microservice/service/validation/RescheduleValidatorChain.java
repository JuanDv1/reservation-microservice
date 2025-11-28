package com.sw3.reservation_microservice.service.validation;

import com.sw3.reservation_microservice.controller.dto.request.RescheduleReservationRequestDTO;
import com.sw3.reservation_microservice.service.validation.handlers.BarberAvailabilityForRescheduleHandler;
import com.sw3.reservation_microservice.service.validation.handlers.BusinessHoursForRescheduleHandler;
import com.sw3.reservation_microservice.service.validation.handlers.TimeConsistencyForRescheduleHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Construye y ejecuta la cadena de validación para reprogramar reservas.
 * Solo valida tiempo y disponibilidad (no barbero/servicio porque ya existen).
 */
@Component
public class RescheduleValidatorChain {

    @Autowired
    private TimeConsistencyForRescheduleHandler timeConsistencyHandler;

    @Autowired
    private BusinessHoursForRescheduleHandler businessHoursHandler;

    @Autowired
    private BarberAvailabilityForRescheduleHandler availabilityHandler;

    private RescheduleValidatorHandler chain;

    @PostConstruct
    public void buildChain() {
        // Cadena simplificada: tiempo → horario → disponibilidad
        timeConsistencyHandler.setNext(businessHoursHandler);
        businessHoursHandler.setNext(availabilityHandler);

        chain = timeConsistencyHandler;
    }

    /**
     * Ejecuta las validaciones de reprogramación.
     */
    public void validate(RescheduleReservationRequestDTO request, String barberId, Long reservationId) {
        chain.validate(request, barberId, reservationId);
    }
}
