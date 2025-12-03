package com.sw3.reservation_microservice.messaging.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO que representa un evento de dominio relacionado con un Barbero.
 * Estructura compatible con el evento publicado por el microservicio de Barberos.
 */
@Data
@NoArgsConstructor
public class BarberEventDTO {
    private String id; // Cambiado a String para coincidir con BarberEventDTO del publisher
    private Boolean active;
    private List<String> serviceIds; // Lista de servicios que ofrece el barbero
}
