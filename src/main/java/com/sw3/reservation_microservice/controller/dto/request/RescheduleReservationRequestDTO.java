package com.sw3.reservation_microservice.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para reprogramar una reserva existente (cambiar fecha/hora).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleReservationRequestDTO {

    @NotNull(message = "La nueva fecha y hora de inicio son obligatorias")
    private LocalDateTime startTime;

    @NotNull(message = "La nueva fecha y hora de fin son obligatorias")
    private LocalDateTime endTime;
}
