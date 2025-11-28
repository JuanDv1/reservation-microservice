package com.sw3.reservation_microservice.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para reprogramar una reserva (cambiar fecha/hora).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleReservationRequestDTO {

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalDateTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalDateTime endTime;
}
