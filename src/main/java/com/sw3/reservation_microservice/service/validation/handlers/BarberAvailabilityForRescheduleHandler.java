package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.ReservationRepository;
import com.sw3.reservation_microservice.controller.dto.RescheduleReservationRequestDTO;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.service.validation.RescheduleValidatorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Valida que el barbero esté disponible en el nuevo horario (excluyendo la reserva actual).
 */
@Component
public class BarberAvailabilityForRescheduleHandler extends RescheduleValidatorHandler {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    protected void validateConcrete(RescheduleReservationRequestDTO request, String barberId, Long reservationId) {
        // Buscar reservas que se solapen
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
            barberId,
            request.getStartTime(),
            request.getEndTime()
        );

        // Excluir la reserva actual (la que estamos reprogramando)
        List<Reservation> conflictingReservations = overlappingReservations.stream()
            .filter(r -> !r.getId().equals(reservationId))
            .collect(Collectors.toList());

        if (!conflictingReservations.isEmpty()) {
            throw new ReservationValidationException(
                "El barbero no está disponible en el nuevo horario solicitado. Ya tiene otra reserva."
            );
        }
    }
}
