package com.sw3.reservation_microservice.messaging.events;

import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.config.RabbitMqConfig;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import com.sw3.reservation_microservice.messaging.dto.ServiceEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener encargado de sincronizar la tabla espejo de servicios en la base de datos local
 * del microservicio de reservas, a partir de los eventos recibidos por RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceEventListener {

    private final ServiceRepository serviceRepository;

    /**
     * Escucha eventos de servicios y sincroniza la tabla local.
     * Realiza upsert (crea o actualiza) la entidad ServiceEntity.
     *
     * @param event Evento recibido desde RabbitMQ
     */
    @RabbitListener(queues = RabbitMqConfig.SERVICE_LISTENER_QUEUE)
    @Transactional
    public void handleServiceEvent(ServiceEventDTO event) {
        log.info("[ServiceEventListener] Recibido evento de Servicio: ID={}, Accion=Sincronizar", event.getId());
        try {
            ServiceEntity service = serviceRepository.findById(event.getId()).orElse(new ServiceEntity());
            service.setId(event.getId());
            // Convertir BigDecimal a Double para price
            service.setPrice(event.getPrice() != null ? event.getPrice().doubleValue() : null);
            service.setDuration(event.getDuration());

            boolean availabilityStatus = "Activo".equalsIgnoreCase(event.getAvailabilityStatus()) 
                                       || "Disponible".equalsIgnoreCase(event.getAvailabilityStatus());
            service.setAvailabilityStatus(availabilityStatus);
            serviceRepository.save(service);
            log.info("[ServiceEventListener] Servicio sincronizado en BD local: ID={}, Estado={}", service.getId(), service.getAvailabilityStatus());
        } catch (Exception e) {
            log.error("[ServiceEventListener] Error al procesar evento de servicio: {}", e.getMessage());
        }
    }
}
