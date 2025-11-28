package com.sw3.reservation_microservice.messaging.listeners;

import com.sw3.reservation_microservice.access.BarberRepository;
import com.sw3.reservation_microservice.domain.model.Barber;
import com.sw3.reservation_microservice.messaging.config.RabbitMQConfig;
import com.sw3.reservation_microservice.messaging.events.BarberCreatedEvent;
import com.sw3.reservation_microservice.messaging.events.BarberDeletedEvent;
import com.sw3.reservation_microservice.messaging.events.BarberUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener que escucha eventos de barberos desde RabbitMQ
 * y sincroniza la base de datos local.
 */
@Component
public class BarberEventListener {

    private static final Logger logger = LoggerFactory.getLogger(BarberEventListener.class);

    @Autowired
    private BarberRepository barberRepository;

    /**
     * Escucha eventos de creación de barberos.
     */
    @RabbitListener(queues = RabbitMQConfig.BARBER_QUEUE)
    @Transactional
    public void handleBarberCreated(BarberCreatedEvent event) {
        logger.info("Evento recibido - Barbero creado: {}", event.getBarberId());
        
        try {
            Barber barber = new Barber();
            barber.setBarberId(event.getBarberId());
            barber.setName(event.getName());
            barber.setServiceIds(event.getServiceIds());
            barber.setAvailabilityStatus(event.getAvailabilityStatus());
            barber.setSystemStatus(event.getSystemStatus());
            barber.setActive("Activo".equals(event.getSystemStatus()));
            
            barberRepository.save(barber);
            logger.info("Barbero {} sincronizado correctamente en BD local", event.getBarberId());
        } catch (Exception e) {
            logger.error("Error al sincronizar barbero creado: {}", event.getBarberId(), e);
        }
    }

    /**
     * Escucha eventos de actualización de barberos.
     */
    @RabbitListener(queues = RabbitMQConfig.BARBER_QUEUE)
    @Transactional
    public void handleBarberUpdated(BarberUpdatedEvent event) {
        logger.info("Evento recibido - Barbero actualizado: {}", event.getBarberId());
        
        try {
            Barber barber = barberRepository.findById(event.getBarberId())
                    .orElse(new Barber());
            
            barber.setBarberId(event.getBarberId());
            barber.setName(event.getName());
            barber.setServiceIds(event.getServiceIds());
            barber.setAvailabilityStatus(event.getAvailabilityStatus());
            barber.setSystemStatus(event.getSystemStatus());
            barber.setActive("Activo".equals(event.getSystemStatus()));
            
            barberRepository.save(barber);
            logger.info("Barbero {} actualizado correctamente en BD local", event.getBarberId());
        } catch (Exception e) {
            logger.error("Error al sincronizar barbero actualizado: {}", event.getBarberId(), e);
        }
    }

    /**
     * Escucha eventos de eliminación de barberos.
     */
    @RabbitListener(queues = RabbitMQConfig.BARBER_QUEUE)
    @Transactional
    public void handleBarberDeleted(BarberDeletedEvent event) {
        logger.info("Evento recibido - Barbero eliminado: {}", event.getBarberId());
        
        try {
            barberRepository.deleteById(event.getBarberId());
            logger.info("Barbero {} eliminado correctamente de BD local", event.getBarberId());
        } catch (Exception e) {
            logger.error("Error al eliminar barbero: {}", event.getBarberId(), e);
        }
    }
}
