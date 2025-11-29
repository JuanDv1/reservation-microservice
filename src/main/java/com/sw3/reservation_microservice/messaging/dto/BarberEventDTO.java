package com.sw3.reservation_microservice.messaging.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un evento de dominio relacionado con un Barbero.
 * Estructura compatible con el evento publicado por el microservicio de Barberos.
 */
@Data
@NoArgsConstructor
public class BarberEventDTO {
    private Long id;
    private Boolean active;
}
