package com.sw3.reservation_microservice.domain.state;

import com.sw3.reservation_microservice.domain.model.Reservation;

public interface ReservationState {
    void cancelar(Reservation reservation);
    void iniciarServicio(Reservation reservation);
    void finalizarServicio(Reservation reservation);
}
