package com.sw3.reservation_microservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Evento publicado cuando se actualiza un barbero.
 * Contiene solo los datos necesarios para el contexto de reservas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarberUpdatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String barberId;
    private String name;
    private String serviceIds; // CSV: "101,102,107"
    private String availabilityStatus; // "Disponible" | "No Disponible"
    private String systemStatus; // "Activo" | "Inactivo"
}
