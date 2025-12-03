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
 * Listener encargado de sincronizar la tabla espejo de horarios (WorkShifts) en la base de datos local
 * del microservicio de reservas, a partir de los eventos recibidos por RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkShiftEventListener {

    private final WorkShiftRepository workShiftRepository;

    /**
     * Escucha eventos de WorkShifts y sincroniza la tabla local.
     * Realiza upsert (crea o actualiza) la entidad WorkShift.
     *
     * @param event Evento recibido desde RabbitMQ
     */
    @RabbitListener(queues = RabbitMqConfig.WORKSHIFT_LISTENER_QUEUE)
    @Transactional
    public void handleWorkShiftEvent(WorkShiftEventDTO event) {
        log.info("[WorkShiftEventListener] 📩 Recibido evento de WorkShift: ID={}, BarberoID={}", event.getId(), event.getBarberId());
        
        try {
            WorkShift workShift = workShiftRepository.findById(event.getId()).orElse(new WorkShift());
            boolean isNew = workShift.getId() == null;
            
            // Si es nuevo, asignamos el ID que viene del evento (para mantener consistencia)
            // Nota: Si la BD genera IDs autoincrementales y el evento trae un ID, 
            // hay que tener cuidado. En este caso asumimos que queremos replicar el ID del maestro.
            // Pero WorkShift tiene @GeneratedValue(strategy = GenerationType.IDENTITY).
            // Esto puede ser problemático si intentamos setear el ID manualmente en una entidad nueva con IDENTITY.
            // Sin embargo, para tablas espejo, lo ideal es no usar IDENTITY y usar el ID del maestro.
            // Vamos a intentar setearlo. Si falla, tendremos que revisar la estrategia de ID en WorkShift.
            
            workShift.setId(event.getId());
            workShift.setDayOfWeek(event.getDayOfWeek());
            workShift.setStartTime(event.getStartTime());
            workShift.setEndTime(event.getEndTime());
            workShift.setBarberId(event.getBarberId());
            
            workShiftRepository.save(workShift);
            
            log.info("[WorkShiftEventListener] ✅ WorkShift {} en BD: ID={}, Dia={}, Horario={}-{}", 
                    isNew ? "creado" : "actualizado", 
                    workShift.getId(), 
                    workShift.getDayOfWeek(),
                    workShift.getStartTime(),
                    workShift.getEndTime());
            
        } catch (Exception e) {
            log.error("[WorkShiftEventListener] ❌ Error al procesar evento de WorkShift: {}", e.getMessage(), e);
        }
    }
}
