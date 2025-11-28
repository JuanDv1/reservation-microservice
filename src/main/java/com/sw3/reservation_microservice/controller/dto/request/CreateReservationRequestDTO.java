package com.sw3.reservation_microservice.controller.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear una nueva reserva.
 * Representa la petición del cliente desde el frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequestDTO {

    @NotBlank(message = "El ID del cliente es obligatorio")
    private String clientId;

    @NotBlank(message = "El ID del barbero es obligatorio")
    private String barberId;

    @NotNull(message = "El ID del servicio es obligatorio")
    private Long serviceId;

    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    private LocalDateTime startTime;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double price;
}
