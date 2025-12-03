package com.sw3.reservation_microservice.messaging.events;

import com.sw3.reservation_microservice.access.BarberRepository;
import com.sw3.reservation_microservice.access.BarberServiceRepository;
import com.sw3.reservation_microservice.config.RabbitMqConfig;
import com.sw3.reservation_microservice.domain.model.Barber;
import com.sw3.reservation_microservice.domain.model.BarberService;
import com.sw3.reservation_microservice.messaging.dto.BarberEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Listener encargado de sincronizar la tabla espejo de barberos en la base de datos local
 * del microservicio de reservas, a partir de los eventos recibidos por RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BarberEventListener {

    private final BarberRepository barberRepository;
    private final BarberServiceRepository barberServiceRepository;

    /**
     * Escucha eventos de barberos y sincroniza la tabla local.
     * Realiza upsert (crea o actualiza) la entidad Barber y sincroniza BarberService.
     *
     * @param event Evento recibido desde RabbitMQ
     */
    @RabbitListener(queues = RabbitMqConfig.BARBER_LISTENER_QUEUE)
    @Transactional
    public void handleBarberEvent(BarberEventDTO event) {
        log.info("[BarberEventListener] Recibido evento de Barbero: ID={}, Accion=Sincronizar", event.getId());
        try {
            // 1. Sincronizar Barber
            Barber barber = barberRepository.findById(event.getId()).orElse(new Barber());
            barber.setId(event.getId());
            barber.setAvailabilityStatus(event.getActive());
            barberRepository.save(barber);
            log.info("[BarberEventListener] Barbero sincronizado en BD local: ID={}, Estado={}", barber.getId(), barber.getAvailabilityStatus());
            
            // 2. Sincronizar BarberService (relación barbero-servicios)
            if (event.getServiceIds() != null) {
                // Convertir serviceIds de String a Long
                List<Long> incomingServiceIds = event.getServiceIds().stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
                
                // Obtener servicios actuales del barbero
                List<BarberService> currentServices = barberServiceRepository.findByBarberId(event.getId());
                List<Long> currentServiceIds = currentServices.stream()
                    .map(BarberService::getServiceId)
                    .collect(Collectors.toList());
                
                // Servicios a agregar
                List<Long> servicesToAdd = incomingServiceIds.stream()
                    .filter(serviceId -> !currentServiceIds.contains(serviceId))
                    .collect(Collectors.toList());
                
                // Servicios a desactivar (los que ya no están en la lista)
                List<Long> servicesToDeactivate = currentServiceIds.stream()
                    .filter(serviceId -> !incomingServiceIds.contains(serviceId))
                    .collect(Collectors.toList());
                
                // Agregar nuevos servicios
                for (Long serviceId : servicesToAdd) {
                    BarberService barberService = new BarberService();
                    barberService.setBarberId(event.getId());
                    barberService.setServiceId(serviceId);
                    barberService.setActive(true);
                    barberServiceRepository.save(barberService);
                    log.info("[BarberEventListener] BarberService creado: BarberId={}, ServiceId={}", event.getId(), serviceId);
                }
                
                // Desactivar servicios que ya no aplican
                for (Long serviceId : servicesToDeactivate) {
                    barberServiceRepository.findByBarberIdAndServiceId(event.getId(), serviceId)
                        .ifPresent(bs -> {
                            bs.setActive(false);
                            barberServiceRepository.save(bs);
                            log.info("[BarberEventListener] BarberService desactivado: BarberId={}, ServiceId={}", event.getId(), serviceId);
                        });
                }
                
                // Reactivar servicios que vuelven a estar en la lista
                for (Long serviceId : incomingServiceIds) {
                    if (currentServiceIds.contains(serviceId)) {
                        barberServiceRepository.findByBarberIdAndServiceId(event.getId(), serviceId)
                            .ifPresent(bs -> {
                                if (!bs.getActive()) {
                                    bs.setActive(true);
                                    barberServiceRepository.save(bs);
                                    log.info("[BarberEventListener] BarberService reactivado: BarberId={}, ServiceId={}", event.getId(), serviceId);
                                }
                            });
                    }
                }
            }
        } catch (Exception e) {
            log.error("[BarberEventListener] Error al procesar evento de barbero: {}", e.getMessage(), e);
        }
    }
}
