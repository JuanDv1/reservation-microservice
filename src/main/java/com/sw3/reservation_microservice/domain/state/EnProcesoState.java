package com.sw3.reservation_microservice.domain.state;

import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.CancellationNotAllowedException;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.InvalidReservationStateException;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.domain.model.ReservationStatus;

public class EnProcesoState implements ReservationState {

    @Override
    public void cancelar(Reservation reservation) {
        throw new CancellationNotAllowedException("No se puede cancelar una reserva que ya está en proceso.");
    }

    @Override
    public void iniciarServicio(Reservation reservation) {
        throw new InvalidReservationStateException("El servicio ya ha sido iniciado.");
    }

    @Override
    public void finalizarServicio(Reservation reservation) {
        reservation.setStatus(ReservationStatus.FINALIZADA);
        reservation.setState(new FinalizadaState());
    }
}
