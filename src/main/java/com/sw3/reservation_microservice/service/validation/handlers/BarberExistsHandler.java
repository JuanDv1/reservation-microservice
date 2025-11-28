package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.BarberRepository;
import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.domain.model.Barber;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Valida que el barbero exista en la base de datos local y esté activo.
 */
@Component
public class BarberExistsHandler extends BaseValidatorHandler {

    @Autowired
    private BarberRepository barberRepository;

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        String barberId = request.getBarberId();
        
        // Verificar que el barbero existe
        Optional<Barber> barberOpt = barberRepository.findById(barberId);
        
        if (barberOpt.isEmpty()) {
            throw new ReservationValidationException(
                String.format("El barbero con ID '%s' no existe en el sistema.", barberId)
            );
        }
        
        Barber barber = barberOpt.get();
        
        // Verificar que el barbero está activo
        if (!barber.getActive()) {
            throw new ReservationValidationException(
                String.format("El barbero '%s' no está activo y no puede recibir reservas.", barber.getName())
            );
        }
    }
}
