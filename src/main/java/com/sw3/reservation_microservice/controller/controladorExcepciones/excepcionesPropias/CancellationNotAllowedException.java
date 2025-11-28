package com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias;

import com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones.CodigoError;

public class CancellationNotAllowedException extends GestionClientesRuntimeException {

  private static final String FORMATO_EXCEPCION = "%s - Cancelación no permitida: %s";

  private final String mensajeCancelacion;

  public CancellationNotAllowedException(final String mensajeCancelacion) {
    super(CodigoError.CANCELACION_NO_PERMITIDA);
    this.mensajeCancelacion = mensajeCancelacion;
  }

  @Override
  public String formatException() {
    return String.format(FORMATO_EXCEPCION, codigoError.getCodigo(), mensajeCancelacion);
  }
}
