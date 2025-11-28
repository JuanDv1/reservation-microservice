package com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias;

import com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones.CodigoError;

public class ReservationNotFoundException extends GestionClientesRuntimeException {
    
    private static final String FORMATO_EXCEPCION = "%s - Reserva no encontrada o no pertenece al cliente: ID=%d, ClientID=%s";

    private final Long reservationId;
    private final String clientId;

    public ReservationNotFoundException(Long reservationId, String clientId) {
        super(CodigoError.RESERVA_NO_ENCONTRADA);
        this.reservationId = reservationId;
        this.clientId = clientId;
    }

    @Override
    public String formatException() {
        return String.format(FORMATO_EXCEPCION, codigoError.getCodigo(), reservationId, clientId);
    }
}
