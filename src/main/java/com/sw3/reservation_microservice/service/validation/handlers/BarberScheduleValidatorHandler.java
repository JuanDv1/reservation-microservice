package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.BarberRepository;
import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.domain.model.Barber;
import com.sw3.reservation_microservice.domain.model.BarberSchedule;
import com.sw3.reservation_microservice.domain.model.BarberShift;
import com.sw3.reservation_microservice.service.validation.BaseValidatorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Valida que la reserva esté dentro del horario específico del barbero.
 * Verifica:
 * - El barbero trabaja ese día de la semana
 * - La hora de la reserva cae en uno de los turnos del barbero
 */
@Component
public class BarberScheduleValidatorHandler extends BaseValidatorHandler {

    @Autowired
    private BarberRepository barberRepository;

    @Override
    protected void validateConcrete(CreateReservationRequestDTO request) {
        // Obtener el barbero
        Barber barber = barberRepository.findById(request.getBarberId())
            .orElseThrow(() -> new ReservationValidationException("Barbero no encontrado"));

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        // Convertir DayOfWeek a número (0=Domingo, 1=Lunes, ..., 6=Sábado)
        int dayOfWeek = convertDayOfWeek(startTime.getDayOfWeek());

        // Buscar el horario del barbero para ese día
        BarberSchedule scheduleForDay = barber.getSchedule().stream()
            .filter(schedule -> schedule.getDayOfWeek().equals(dayOfWeek))
            .findFirst()
            .orElse(null);

        // Si no tiene horario configurado para ese día
        if (scheduleForDay == null || scheduleForDay.getShifts().isEmpty()) {
            throw new ReservationValidationException(
                String.format("El barbero %s no trabaja los %s", 
                    barber.getName(), 
                    getDayName(dayOfWeek))
            );
        }

        // Verificar que la reserva caiga en alguno de los turnos del barbero
        LocalTime startLocalTime = startTime.toLocalTime();
        LocalTime endLocalTime = endTime.toLocalTime();

        boolean isWithinShift = scheduleForDay.getShifts().stream()
            .anyMatch(shift -> isTimeInShift(startLocalTime, endLocalTime, shift));

        if (!isWithinShift) {
            String availableShifts = formatAvailableShifts(scheduleForDay.getShifts());
            throw new ReservationValidationException(
                String.format("La reserva (%s - %s) no está dentro del horario del barbero. Turnos disponibles: %s",
                    startLocalTime, endLocalTime, availableShifts)
            );
        }
    }

    /**
     * Verifica si un rango de tiempo está completamente dentro de un turno.
     */
    private boolean isTimeInShift(LocalTime start, LocalTime end, BarberShift shift) {
        return !start.isBefore(shift.getStartTime()) && !end.isAfter(shift.getEndTime());
    }

    /**
     * Convierte DayOfWeek de Java a número (0=Domingo, 1=Lunes, ..., 6=Sábado).
     */
    private int convertDayOfWeek(DayOfWeek dayOfWeek) {
        // DayOfWeek: MONDAY=1, TUESDAY=2, ..., SUNDAY=7
        // Queremos: Domingo=0, Lunes=1, ..., Sábado=6
        return dayOfWeek == DayOfWeek.SUNDAY ? 0 : dayOfWeek.getValue();
    }

    /**
     * Obtiene el nombre del día en español.
     */
    private String getDayName(int dayOfWeek) {
        String[] days = {"domingos", "lunes", "martes", "miércoles", "jueves", "viernes", "sábados"};
        return days[dayOfWeek];
    }

    /**
     * Formatea los turnos disponibles para mostrar en el mensaje de error.
     */
    private String formatAvailableShifts(List<BarberShift> shifts) {
        return shifts.stream()
            .map(shift -> shift.getStartTime() + " - " + shift.getEndTime())
            .reduce((a, b) -> a + ", " + b)
            .orElse("Ninguno");
    }
}
