package com.sw3.reservation_microservice.messaging.events;

import com.sw3.reservation_microservice.access.BarberRepository;
import com.sw3.reservation_microservice.config.RabbitMqConfig;
import com.sw3.reservation_microservice.domain.model.Barber;
import com.sw3.reservation_microservice.messaging.dto.BarberEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener encargado de sincronizar la tabla espejo de barberos en la base de datos local
 * del microservicio de reservas, a partir de los eventos recibidos por RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BarberEventListener {

    private final BarberRepository barberRepository;

    /**
     * Escucha eventos de barberos y sincroniza la tabla local.
     * Realiza upsert (crea o actualiza) la entidad Barber.
     *
     * @param event Evento recibido desde RabbitMQ
     */
    @RabbitListener(queues = RabbitMqConfig.BARBER_LISTENER_QUEUE)
    @Transactional
    public void handleBarberEvent(BarberEventDTO event) {
        log.info("[BarberEventListener] Recibido evento de Barbero: ID={}, Accion=Sincronizar", event.getId());
        try {
            // Buscar por ID; si no existe, crear nuevo
            Barber barber = barberRepository.findById(event.getId()).orElse(new Barber());
            barber.setId(event.getId());
            barber.setAvailabilityStatus(event.getActive());
            // Guardar/actualizar en la tabla espejo
            barberRepository.save(barber);
            log.info("[BarberEventListener] Barbero sincronizado en BD local: ID={}, Estado={}", barber.getId(), barber.getAvailabilityStatus());
        } catch (Exception e) {
            log.error("[BarberEventListener] Error al procesar evento de barbero: {}", e.getMessage());
        }
    }
}
