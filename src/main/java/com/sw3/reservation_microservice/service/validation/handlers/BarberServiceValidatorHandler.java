package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.BarberRepository;
import com.sw3.reservation_microservice.access.BarberServiceRepository;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.Barber;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Valida que:
 * 1. El barbero exista
 * 2. El barbero esté activo (availabilityStatus = true)
 * 3. El barbero ofrezca el servicio solicitado (mediante BarberService)
 */
@Component
public class BarberServiceValidatorHandler extends BaseValidatorHandler {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private BarberServiceRepository barberServiceRepository;

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        Long barberId = Long.parseLong(request.getBarberId());
        Long serviceId = request.getServiceId();

        // 1. Verificar que el barbero existe
        Barber barber = barberRepository.findById(barberId)
            .orElseThrow(() -> new ReservationValidationException(
                "El barbero con ID " + barberId + " no existe."
            ));

        // 2. Verificar que el barbero está activo
        if (!barber.getAvailabilityStatus()) {
            throw new ReservationValidationException(
                "El barbero con ID " + barberId + " no está activo actualmente."
            );
        }

        // 3. Verificar que el barbero ofrece el servicio solicitado
        boolean offersService = barberServiceRepository
            .existsByBarberIdAndServiceIdAndActiveTrue(barberId, serviceId);

        if (!offersService) {
            throw new ReservationValidationException(
                "El barbero con ID " + barberId + " no ofrece el servicio con ID " + serviceId + "."
            );
        }
    }
}
