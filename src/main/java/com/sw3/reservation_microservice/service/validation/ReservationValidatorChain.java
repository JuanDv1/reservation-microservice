package com.sw3.reservation_microservice.service.validation;

import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.service.validation.handlers.BarberAvailabilityHandler;
import com.sw3.reservation_microservice.service.validation.handlers.BarberExistsHandler;
import com.sw3.reservation_microservice.service.validation.handlers.BarberScheduleValidatorHandler;
import com.sw3.reservation_microservice.service.validation.handlers.RequiredFieldsHandler;
import com.sw3.reservation_microservice.service.validation.handlers.ServiceExistsHandler;
import com.sw3.reservation_microservice.service.validation.handlers.TimeConsistencyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Construye y ejecuta la cadena de validación de reservas.
 */
@Component
public class ReservationValidatorChain {

    @Autowired
    private RequiredFieldsHandler requiredFieldsHandler;

    @Autowired
    private TimeConsistencyHandler timeConsistencyHandler;

    @Autowired
    private BarberScheduleValidatorHandler barberScheduleValidatorHandler;

    @Autowired
    private BarberExistsHandler barberExistsHandler;

    @Autowired
    private ServiceExistsHandler serviceExistsHandler;

    @Autowired
    private BarberAvailabilityHandler barberAvailabilityHandler;

    private ReservationValidatorHandler chain;

    @PostConstruct
    public void buildChain() {
        // Construir la cadena de responsabilidades
        // Orden: 
        // 1. Campos requeridos
        // 2. Consistencia de tiempo
        // 3. Barbero existe y está activo
        // 4. Servicio existe y está activo
        // 5. Horario del barbero (valida schedule específico)
        // 6. Disponibilidad del barbero
        requiredFieldsHandler.setNext(timeConsistencyHandler);
        timeConsistencyHandler.setNext(barberExistsHandler);
        barberExistsHandler.setNext(serviceExistsHandler);
        serviceExistsHandler.setNext(barberScheduleValidatorHandler);
        barberScheduleValidatorHandler.setNext(barberAvailabilityHandler);

        chain = requiredFieldsHandler;
    }

    /**
     * Ejecuta todas las validaciones en la cadena.
     * Si alguna validación falla, lanza una excepción.
     */
    public void validate(CreateReservationRequestDTO request) {
        chain.validate(request);
    }
}
