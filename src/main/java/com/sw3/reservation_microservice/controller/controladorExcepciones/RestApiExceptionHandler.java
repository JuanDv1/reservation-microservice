package com.sw3.reservation_microservice.controller.controladorExcepciones;

import java.util.Locale;

import com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones.Error;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones.CodigoError;
import com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones.ErrorUtils;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.EntidadNoExisteException;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.EntidadYaExisteException;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReglaNegocioExcepcion;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.InvalidReservationStateException;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.CancellationNotAllowedException;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationNotFoundException;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.InvalidReservationDeletionException;

@ControllerAdvice
public class RestApiExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Error> handleGenericException(final HttpServletRequest req,
                        final Exception ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.ERROR_GENERICO.getCodigo(),
                                                CodigoError.ERROR_GENERICO.getLlaveMensaje(),
                                                HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(EntidadYaExisteException.class)
        public ResponseEntity<Error> handleGenericException(final HttpServletRequest req,
                        final EntidadYaExisteException ex) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.ENTIDAD_YA_EXISTE.getCodigo(),
                                                String.format("%s, %s", CodigoError.ENTIDAD_YA_EXISTE.getLlaveMensaje(),
                                                                ex.getMessage()),
                                                HttpStatus.NOT_ACCEPTABLE.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }

        @ExceptionHandler(ReglaNegocioExcepcion.class)
        public ResponseEntity<Error> handleGenericException(final HttpServletRequest req,
                        final ReglaNegocioExcepcion ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.VIOLACION_REGLA_DE_NEGOCIO.getCodigo(), ex.formatException(),
                                                HttpStatus.BAD_REQUEST.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(EntidadNoExisteException.class)
        public ResponseEntity<Error> handleGenericException(final HttpServletRequest req,
                        final EntidadNoExisteException ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.ENTIDAD_NO_ENCONTRADA.getCodigo(),
                                                String.format("%s, %s",
                                                                CodigoError.ENTIDAD_NO_ENCONTRADA.getLlaveMensaje(),
                                                                ex.getMessage()),
                                                HttpStatus.NOT_FOUND.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(ReservationValidationException.class)
        public ResponseEntity<Error> handleReservationValidationException(final HttpServletRequest req,
                        final ReservationValidationException ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.VALIDACION_RESERVA.getCodigo(), ex.formatException(),
                                                HttpStatus.BAD_REQUEST.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(InvalidReservationStateException.class)
        public ResponseEntity<Error> handleInvalidReservationStateException(final HttpServletRequest req,
                        final InvalidReservationStateException ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.ESTADO_RESERVA_INVALIDO.getCodigo(), ex.formatException(),
                                                HttpStatus.BAD_REQUEST.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(CancellationNotAllowedException.class)
        public ResponseEntity<Error> handleCancellationNotAllowedException(final HttpServletRequest req,
                        final CancellationNotAllowedException ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.CANCELACION_NO_PERMITIDA.getCodigo(), ex.formatException(),
                                                HttpStatus.BAD_REQUEST.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ReservationNotFoundException.class)
        public ResponseEntity<Error> handleReservationNotFoundException(final HttpServletRequest req,
                        final ReservationNotFoundException ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.RESERVA_NO_ENCONTRADA.getCodigo(), ex.formatException(),
                                                HttpStatus.NOT_FOUND.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(InvalidReservationDeletionException.class)
        public ResponseEntity<Error> handleInvalidReservationDeletionException(final HttpServletRequest req,
                        final InvalidReservationDeletionException ex, final Locale locale) {
                final Error error = ErrorUtils
                                .crearError(CodigoError.ELIMINACION_NO_PERMITIDA.getCodigo(), ex.formatException(),
                                                HttpStatus.BAD_REQUEST.value())
                                .setUrl(req.getRequestURL().toString()).setMetodo(req.getMethod());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

}
