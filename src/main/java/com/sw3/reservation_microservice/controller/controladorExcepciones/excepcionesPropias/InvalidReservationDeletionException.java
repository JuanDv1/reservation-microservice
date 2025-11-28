package com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias;

import com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones.CodigoError;
import com.sw3.reservation_microservice.domain.model.ReservationStatus;

public class InvalidReservationDeletionException extends GestionClientesRuntimeException {
    
    private static final String FORMATO_EXCEPCION = "%s - Solo se pueden eliminar reservas canceladas o finalizadas. Estado actual: %s";

    private final ReservationStatus currentStatus;

    public InvalidReservationDeletionException(ReservationStatus currentStatus) {
        super(CodigoError.ELIMINACION_NO_PERMITIDA);
        this.currentStatus = currentStatus;
    }

    @Override
    public String formatException() {
        return String.format(FORMATO_EXCEPCION, codigoError.getCodigo(), currentStatus);
    }
}
