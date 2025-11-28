package com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias;

import com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones.CodigoError;

public class ReservationValidationException extends GestionClientesRuntimeException {

  private static final String FORMATO_EXCEPCION = "%s - Error de validación: %s";

  private final String mensajeValidacion;

  public ReservationValidationException(final String mensajeValidacion) {
    super(CodigoError.VALIDACION_RESERVA);
    this.mensajeValidacion = mensajeValidacion;
  }

  @Override
  public String formatException() {
    return String.format(FORMATO_EXCEPCION, codigoError.getCodigo(), mensajeValidacion);
  }
}
