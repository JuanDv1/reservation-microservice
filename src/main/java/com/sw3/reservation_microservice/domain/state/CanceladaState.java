package com.sw3.reservation_microservice.domain.state;

import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.InvalidReservationStateException;
import com.sw3.reservation_microservice.domain.model.Reservation;

public class CanceladaState implements ReservationState {

    @Override
    public void cancelar(Reservation reservation) {
        throw new InvalidReservationStateException("La reserva ya está cancelada.");
    }

    @Override
    public void iniciarServicio(Reservation reservation) {
        throw new InvalidReservationStateException("No se puede iniciar un servicio cancelado.");
    }

    @Override
    public void finalizarServicio(Reservation reservation) {
        throw new InvalidReservationStateException("No se puede finalizar un servicio cancelado.");
    }
}
