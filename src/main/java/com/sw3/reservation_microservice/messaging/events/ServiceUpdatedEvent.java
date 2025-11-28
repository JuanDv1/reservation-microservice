package com.sw3.reservation_microservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Evento publicado cuando se actualiza un servicio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUpdatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long serviceId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean active;
}
