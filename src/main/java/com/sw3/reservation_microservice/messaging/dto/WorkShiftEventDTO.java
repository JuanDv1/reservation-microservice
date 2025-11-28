package com.sw3.reservation_microservice.messaging.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

/**
 * DTO que representa un evento de dominio relacionado con un WorkShift.
 * Estructura compatible con el evento publicado por el microservicio de Barberos.
 */
@Data
@NoArgsConstructor
public class WorkShiftEventDTO {
    private Long id;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long barberId;
}
