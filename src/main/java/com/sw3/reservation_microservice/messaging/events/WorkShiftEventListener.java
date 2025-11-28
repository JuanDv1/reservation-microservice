package com.sw3.reservation_microservice.messaging.events;

import com.sw3.reservation_microservice.access.WorkShiftRepository;
import com.sw3.reservation_microservice.config.RabbitMqConfig;
import com.sw3.reservation_microservice.domain.model.WorkShift;
import com.sw3.reservation_microservice.messaging.dto.WorkShiftEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Listener encargado de sincronizar la tabla espejo de workshifts en la base de datos local
 * del microservicio de reservas, a partir de los eventos recibidos por RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkShiftEventListener {

    private final WorkShiftRepository workShiftRepository;

    /**
     * Escucha eventos de workshifts y sincroniza la tabla local.
     * Realiza upsert (crea o actualiza) la entidad WorkShift.
     *
     * @param event Evento recibido desde RabbitMQ
     */
    @RabbitListener(queues = RabbitMqConfig.WORKSHIFT_LISTENER_QUEUE)
    @Transactional
    public void handleWorkShiftEvent(WorkShiftEventDTO event) {
        log.info("[WorkShiftEventListener] Recibido evento de WorkShift: ID={}, Accion=Sincronizar", event.getId());
        try {
            WorkShift workShift = workShiftRepository.findById(event.getId()).orElse(new WorkShift());
            workShift.setId(event.getId());
            workShift.setDayOfWeek(event.getDayOfWeek());
            workShift.setStartTime(event.getStartTime());
            workShift.setEndTime(event.getEndTime());
            workShift.setBarberId(event.getBarberId());
            workShiftRepository.save(workShift);
            log.info("[WorkShiftEventListener] WorkShift sincronizado en BD local: ID={}, BarberId={}", workShift.getId(), workShift.getBarberId());
        } catch (Exception e) {
            log.error("[WorkShiftEventListener] Error al procesar evento de workshift: {}", e.getMessage());
        }
    }
}
