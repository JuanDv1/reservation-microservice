package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.WorkShiftRepository;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.RescheduleReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.WorkShift;
import com.sw3.reservation_microservice.service.validation.RescheduleValidatorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Valida que la nueva fecha de reprogramación esté dentro de los horarios disponibles del barbero (WorkShift).
 */
@Component
public class BusinessHoursForRescheduleHandler extends RescheduleValidatorHandler {

    @Autowired
    private WorkShiftRepository workShiftRepository;

    @Override
    protected void validateConcrete(RescheduleReservationRequestDTO request, String barberId, Long reservationId) {
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();

        // Determinar el día de la semana
        DayOfWeek dow = start.getDayOfWeek();
        String dayKey = dow.name();

        // Buscar turnos del barbero para ese día
        List<WorkShift> shifts = workShiftRepository.findShiftsForDay(barberId, dayKey);
        
        if (shifts.isEmpty()) {
            throw new ReservationValidationException(
                "El barbero no tiene turnos configurados para " + getDayNameSpanish(dow) + "."
            );
        }

        // Verificar que la nueva fecha caiga dentro de algún turno
        boolean fits = shifts.stream().anyMatch(ws ->
                !start.toLocalTime().isBefore(ws.getStartTime()) &&
                !end.toLocalTime().isAfter(ws.getEndTime())
        );

        if (!fits) {
            String availableShifts = shifts.stream()
                .map(ws -> ws.getStartTime() + " - " + ws.getEndTime())
                .collect(Collectors.joining(", "));
            
            throw new ReservationValidationException(
                String.format("La nueva fecha (%s - %s) debe estar dentro del horario disponible del barbero. " +
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
