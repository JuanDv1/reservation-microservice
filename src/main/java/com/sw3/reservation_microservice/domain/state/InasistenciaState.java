package com.sw3.reservation_microservice.domain.state;

import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.CancellationNotAllowedException;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.InvalidReservationStateException;
import com.sw3.reservation_microservice.domain.model.Reservation;

public class InasistenciaState implements ReservationState {

    @Override
    public void cancelar(Reservation reservation) {
        throw new CancellationNotAllowedException("No se puede cancelar una reserva marcada como inasistencia.");
    }

    @Override
    public void iniciarServicio(Reservation reservation) {
        throw new InvalidReservationStateException("No se puede iniciar un servicio marcado como inasistencia.");
    }

    @Override
    public void finalizarServicio(Reservation reservation) {
        throw new InvalidReservationStateException("No se puede finalizar un servicio marcado como inasistencia.");
    }
}
