package com.sw3.reservation_microservice.domain.state;

import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.CancellationNotAllowedException;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.InvalidReservationStateException;
import com.sw3.reservation_microservice.domain.model.Reservation;

public class FinalizadaState implements ReservationState {

    @Override
    public void cancelar(Reservation reservation) {
        throw new CancellationNotAllowedException("No se puede cancelar una reserva finalizada.");
    }

    @Override
    public void iniciarServicio(Reservation reservation) {
        throw new InvalidReservationStateException("El servicio ya ha finalizado.");
    }

    @Override
    public void finalizarServicio(Reservation reservation) {
        throw new InvalidReservationStateException("El servicio ya está finalizado.");
    }
}
