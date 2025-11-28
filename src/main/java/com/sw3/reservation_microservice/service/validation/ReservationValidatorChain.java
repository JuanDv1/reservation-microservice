package com.sw3.reservation_microservice.service.validation;

import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.service.validation.handlers.BarberAvailabilityHandler;
import com.sw3.reservation_microservice.service.validation.handlers.BarberServiceValidatorHandler;
import com.sw3.reservation_microservice.service.validation.handlers.RequiredFieldsHandler;
import com.sw3.reservation_microservice.service.validation.handlers.ServiceValidatorHandler;
import com.sw3.reservation_microservice.service.validation.handlers.TimeConsistencyHandler;
import com.sw3.reservation_microservice.service.validation.handlers.WorkShiftValidatorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Construye y ejecuta la cadena de validación de reservas.
 * 
 * Orden de validación optimizado:
 * 1. Campos requeridos (validación básica de datos)
 * 2. Consistencia de tiempo (fechas válidas y futuras)
 * 3. Servicio existe y está activo
 * 4. Barbero existe, está activo y ofrece el servicio
 * 5. Horarios disponibles del barbero (WorkShift)
 * 6. Disponibilidad del barbero (sin solapamiento de reservas)
 */
@Component
public class ReservationValidatorChain {

    @Autowired
    private RequiredFieldsHandler requiredFieldsHandler;

    @Autowired
    private TimeConsistencyHandler timeConsistencyHandler;

    @Autowired
    private ServiceValidatorHandler serviceValidatorHandler;

    @Autowired
    private BarberServiceValidatorHandler barberServiceValidatorHandler;

    @Autowired
    private WorkShiftValidatorHandler workShiftValidatorHandler;

    @Autowired
    private BarberAvailabilityHandler barberAvailabilityHandler;

    private ReservationValidatorHandler chain;

    @PostConstruct
    public void buildChain() {
        // Construir la cadena de responsabilidades en orden óptimo
        requiredFieldsHandler.setNext(timeConsistencyHandler);
        timeConsistencyHandler.setNext(serviceValidatorHandler);
        serviceValidatorHandler.setNext(barberServiceValidatorHandler);
        barberServiceValidatorHandler.setNext(workShiftValidatorHandler);
        workShiftValidatorHandler.setNext(barberAvailabilityHandler);

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
