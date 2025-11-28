package com.sw3.reservation_microservice.messaging.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para eventos de reserva publicados en RabbitMQ, compatible con el microservicio de servicios.
 */
@Data
@NoArgsConstructor
public class ReservationEventDTO {
    private Long id;
    private Long serviceId;
    private Long barberId;
    private LocalDateTime start;
    private String status;
}
