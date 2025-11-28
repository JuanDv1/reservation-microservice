package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.WorkShiftRepository;
import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.WorkShift;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import com.sw3.reservation_microservice.utils.ReservationTimeCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Valida que la reserva esté dentro de los horarios disponibles del barbero (WorkShift).
 * Verifica que el barbero tenga turnos configurados para el día y que la reserva
 * caiga completamente dentro de uno de esos turnos.
 */
@Component
public class WorkShiftValidatorHandler extends BaseValidatorHandler {

    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        LocalDateTime start = request.getStartTime();
        Long barberId = Long.parseLong(request.getBarberId());

        // Obtener el servicio para calcular el endTime
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new ReservationValidationException("Servicio no encontrado."));

        // Calcular el endTime basado en la duración del servicio
        LocalDateTime end = ReservationTimeCalculator.calculateEndTime(
            start,
            service.getDuration()
        );

        // Determinar el día de la semana usando nombre de enum estándar MONDAY..SUNDAY
        DayOfWeek dow = start.getDayOfWeek();
        String dayKey = dow.name();

        // Buscar turnos del barbero para ese día
        List<WorkShift> shifts = workShiftRepository.findShiftsForDay(barberId, dayKey);
        
        if (shifts.isEmpty()) {
            throw new ReservationValidationException(
                "El barbero no tiene turnos configurados para " + getDayNameSpanish(dow) + "."
            );
        }

        // Verificar que la reserva caiga dentro de algún turno
        boolean fits = shifts.stream().anyMatch(ws ->
                !start.toLocalTime().isBefore(ws.getStartTime()) &&
                !end.toLocalTime().isAfter(ws.getEndTime())
        );

        if (!fits) {
            String availableShifts = shifts.stream()
                .map(ws -> ws.getStartTime() + " - " + ws.getEndTime())
                .collect(Collectors.joining(", "));
            
            throw new ReservationValidationException(
                String.format("La reserva (%s - %s) debe estar dentro del horario disponible del barbero. " +
                             "Turnos disponibles para %s: %s",
                    start.toLocalTime(), end.toLocalTime(), getDayNameSpanish(dow), availableShifts)
            );
        }
    }

    /**
     * Convierte el DayOfWeek a nombre en español.
     */
    private String getDayNameSpanish(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> "lunes";
            case TUESDAY -> "martes";
            case WEDNESDAY -> "miércoles";
            case THURSDAY -> "jueves";
            case FRIDAY -> "viernes";
            case SATURDAY -> "sábado";
            case SUNDAY -> "domingo";
        };
    }
}
