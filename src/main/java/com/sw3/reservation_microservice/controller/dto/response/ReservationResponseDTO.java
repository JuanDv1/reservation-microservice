package com.sw3.reservation_microservice.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para una reserva.
 * Solo incluye IDs de Barber y Service.
 * El frontend compondrá el objeto completo consultando los microservicios correspondientes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {

    /** Identificador único de la reserva */
    private Long id;

    /** ID del cliente que realizó la reserva */
    private String clientId;

    /** ID del barbero asignado */
    private String barberId;

    /** ID del servicio contratado */
    private Long serviceId;

    /** Fecha y hora de inicio de la cita */
    private LocalDateTime start;

    /** Fecha y hora de fin de la cita */
    private LocalDateTime end;

    /** Precio del servicio congelado al momento de la reserva */
    private Double price;

    /** Estado actual de la reserva (En espera, Inasistencia, En proceso, Finalizada, Cancelada) */
    private String status;
}
