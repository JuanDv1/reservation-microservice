package com.sw3.reservation_microservice.messaging.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un evento de dominio relacionado con un Servicio.
 * Estructura compatible con el evento publicado por el microservicio de Servicios.
 */
@Data
@NoArgsConstructor
public class ServiceEventDTO {
    private Long id;
    private Double price;
    private Integer duration;
    private String availabilityStatus;
}
