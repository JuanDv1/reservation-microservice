package com.sw3.reservation_microservice.messaging.listeners;

import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import com.sw3.reservation_microservice.messaging.config.RabbitMQConfig;
import com.sw3.reservation_microservice.messaging.events.ServiceCreatedEvent;
import com.sw3.reservation_microservice.messaging.events.ServiceDeletedEvent;
import com.sw3.reservation_microservice.messaging.events.ServiceUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener que escucha eventos de servicios desde RabbitMQ
 * y sincroniza la base de datos local.
 */
@Component
public class ServiceEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ServiceEventListener.class);

    @Autowired
    private ServiceRepository serviceRepository;

    /**
     * Escucha eventos de creación de servicios.
     */
    @RabbitListener(queues = RabbitMQConfig.SERVICE_QUEUE)
    @Transactional
    public void handleServiceCreated(ServiceCreatedEvent event) {
        logger.info("Evento recibido - Servicio creado: {}", event.getServiceId());
        
        try {
            ServiceEntity service = new ServiceEntity();
            service.setServiceId(event.getServiceId());
            service.setName(event.getName());
            service.setDescription(event.getDescription());
            service.setPrice(event.getPrice());
            service.setDurationMinutes(event.getDurationMinutes());
            service.setActive(event.getActive());
            
            serviceRepository.save(service);
            logger.info("Servicio {} sincronizado correctamente en BD local", event.getServiceId());
        } catch (Exception e) {
            logger.error("Error al sincronizar servicio creado: {}", event.getServiceId(), e);
        }
    }

    /**
     * Escucha eventos de actualización de servicios.
     */
    @RabbitListener(queues = RabbitMQConfig.SERVICE_QUEUE)
    @Transactional
    public void handleServiceUpdated(ServiceUpdatedEvent event) {
        logger.info("Evento recibido - Servicio actualizado: {}", event.getServiceId());
        
        try {
            ServiceEntity service = serviceRepository.findById(event.getServiceId())
                    .orElse(new ServiceEntity());
            
            service.setServiceId(event.getServiceId());
            service.setName(event.getName());
            service.setDescription(event.getDescription());
            service.setPrice(event.getPrice());
            service.setDurationMinutes(event.getDurationMinutes());
            service.setActive(event.getActive());
            
            serviceRepository.save(service);
            logger.info("Servicio {} actualizado correctamente en BD local", event.getServiceId());
        } catch (Exception e) {
            logger.error("Error al sincronizar servicio actualizado: {}", event.getServiceId(), e);
        }
    }

    /**
     * Escucha eventos de eliminación de servicios.
     */
    @RabbitListener(queues = RabbitMQConfig.SERVICE_QUEUE)
    @Transactional
    public void handleServiceDeleted(ServiceDeletedEvent event) {
        logger.info("Evento recibido - Servicio eliminado: {}", event.getServiceId());
        
        try {
            serviceRepository.deleteById(event.getServiceId());
            logger.info("Servicio {} eliminado correctamente de BD local", event.getServiceId());
        } catch (Exception e) {
            logger.error("Error al eliminar servicio: {}", event.getServiceId(), e);
        }
    }
}
