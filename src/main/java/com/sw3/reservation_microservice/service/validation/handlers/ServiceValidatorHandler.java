package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Valida que:
 * 1. El servicio exista
 * 2. El servicio esté activo (availabilityStatus = true)
 */
@Component
public class ServiceValidatorHandler extends BaseValidatorHandler {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        Long serviceId = request.getServiceId();

        // 1. Verificar que el servicio existe
        ServiceEntity service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new ReservationValidationException(
                "El servicio con ID " + serviceId + " no existe."
            ));

        // 2. Verificar que el servicio está activo
        if (!service.getAvailabilityStatus()) {
            throw new ReservationValidationException(
                "El servicio con ID " + serviceId + " no está activo actualmente."
            );
        }
    }
}
