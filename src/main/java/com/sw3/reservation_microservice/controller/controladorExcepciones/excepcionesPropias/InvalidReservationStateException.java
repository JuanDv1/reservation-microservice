package com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias;

import com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones.CodigoError;

public class InvalidReservationStateException extends GestionClientesRuntimeException {

  private static final String FORMATO_EXCEPCION = "%s - Transición de estado inválida: %s";

  private final String mensajeEstado;

  public InvalidReservationStateException(final String mensajeEstado) {
    super(CodigoError.ESTADO_RESERVA_INVALIDO);
    this.mensajeEstado = mensajeEstado;
  }

  @Override
  public String formatException() {
    return String.format(FORMATO_EXCEPCION, codigoError.getCodigo(), mensajeEstado);
  }
}
