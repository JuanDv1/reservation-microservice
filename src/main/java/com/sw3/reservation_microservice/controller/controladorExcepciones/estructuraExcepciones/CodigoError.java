package com.sw3.reservation_microservice.controller.controladorExcepciones.estructuraExcepciones;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CodigoError {

        ERROR_GENERICO("GC-0001", "ERROR GENERICO"),
        ENTIDAD_YA_EXISTE("GC-0002", "ERROR ENTIDAD YA EXISTE"),
        ENTIDAD_NO_ENCONTRADA("GC-0003", "Entidad no encontrada"),
        VIOLACION_REGLA_DE_NEGOCIO("GC-0004", "Regla de negocio violada"),
        CREDENCIALES_INVALIDAS("GC-0005", "Error al iniciar sesión, compruebe sus credenciales y vuelva a intentarlo"),
        USUARIO_DESHABILITADO("GC-0006",
                        "El usuario no ha sido verificado, por favor revise su correo para verificar su cuenta"),
        
        // Códigos específicos para Reservas
        VALIDACION_RESERVA("RES-0001", "Error de validación en la reserva"),
        ESTADO_RESERVA_INVALIDO("RES-0002", "Transición de estado inválida"),
        CANCELACION_NO_PERMITIDA("RES-0003", "No se puede cancelar la reserva"),
        RESERVA_NO_ENCONTRADA("RES-0004", "Reserva no encontrada"),
        ELIMINACION_NO_PERMITIDA("RES-0005", "No se puede eliminar la reserva");

        private final String codigo;
        private final String llaveMensaje;
}
