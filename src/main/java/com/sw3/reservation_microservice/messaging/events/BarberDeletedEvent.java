package com.sw3.reservation_microservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Evento publicado cuando se elimina un barbero.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarberDeletedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String barberId;
}
