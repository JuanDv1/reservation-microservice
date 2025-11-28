package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Valida que el servicio exista en la base de datos local y esté activo.
 */
@Component
public class ServiceExistsHandler extends BaseValidatorHandler {

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        Long serviceId = request.getServiceId();
        
        // Verificar que el servicio existe
        Optional<ServiceEntity> serviceOpt = serviceRepository.findById(serviceId);
        
        if (serviceOpt.isEmpty()) {
            throw new ReservationValidationException(
                String.format("El servicio con ID '%d' no existe en el sistema.", serviceId)
            );
        }
        
        ServiceEntity service = serviceOpt.get();
        
        // Verificar que el servicio está activo
        if (!service.getActive()) {
            throw new ReservationValidationException(
                String.format("El servicio '%s' no está activo y no está disponible para reservas.", service.getName())
            );
        }
    }
}
