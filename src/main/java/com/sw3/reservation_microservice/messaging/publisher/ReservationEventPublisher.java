package com.sw3.reservation_microservice.messaging.publisher;

import com.sw3.reservation_microservice.config.RabbitMqConfig;
import com.sw3.reservation_microservice.messaging.dto.ReservationEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher para enviar eventos de reservas al exchange de RabbitMQ.
 * Compatible con el microservicio de servicios (solo publica los campos requeridos).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventPublisher {

    private final AmqpTemplate amqpTemplate;

    /**
     * Publica un evento de reserva en el exchange de reservas.
     * @param event DTO del evento de reserva (debe tener: id, serviceId, barberId, start, status)
     * @param routingKey Routing key (ej: reservation.created, reservation.updated)
     */
    public void publishEvent(ReservationEventDTO event, String routingKey) {
        log.info("[ReservationEventPublisher] Publicando evento: {} con routingKey: {}", event, routingKey);
        amqpTemplate.convertAndSend(RabbitMqConfig.RESERVATION_EXCHANGE, routingKey, event);
    }
}
