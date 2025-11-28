package com.sw3.reservation_microservice.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta simplificado para listas de reservas.
 * No incluye objetos anidados para optimizar performance en consultas masivas.
 * Usado para historial de reservas, reservas por cliente, reservas del día, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationListItemDTO {

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

    /** Precio del servicio */
    private Double price;

    /** Estado actual de la reserva */
    private String status;
}
