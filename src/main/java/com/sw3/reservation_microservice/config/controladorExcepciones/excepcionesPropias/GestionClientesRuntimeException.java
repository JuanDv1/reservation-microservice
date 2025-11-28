package com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias;

import com.sw3.reservation_microservice.config.controladorExcepciones.estructuraExcepciones.CodigoError;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public abstract class GestionClientesRuntimeException extends RuntimeException {

  protected CodigoError codigoError;

  public abstract String formatException();
}
