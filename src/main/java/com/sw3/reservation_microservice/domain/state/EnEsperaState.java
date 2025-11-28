package com.sw3.reservation_microservice.domain.state;

import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.CancellationNotAllowedException;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.InvalidReservationStateException;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.domain.model.ReservationStatus;

import java.time.LocalDateTime;

public class EnEsperaState implements ReservationState {

    @Override
    public void cancelar(Reservation reservation) {
        // Validar que se cancele con al menos 1 hora de anticipación
        LocalDateTime limiteParaCancelar = reservation.getStartTime().minusHours(1);
        LocalDateTime ahora = LocalDateTime.now();
        
        if (ahora.isAfter(limiteParaCancelar)) {
            throw new CancellationNotAllowedException(
                "No se puede cancelar la reserva. El límite para cancelar es 1 hora antes del inicio."
            );
        }
        
        reservation.setStatus(ReservationStatus.CANCELADA);
        reservation.setState(new CanceladaState());
    }

    @Override
    public void iniciarServicio(Reservation reservation) {
        reservation.setStatus(ReservationStatus.EN_PROCESO);
        reservation.setState(new EnProcesoState());
    }

    @Override
    public void finalizarServicio(Reservation reservation) {
        throw new InvalidReservationStateException("No se puede finalizar un servicio que no ha comenzado.");
    }
}
