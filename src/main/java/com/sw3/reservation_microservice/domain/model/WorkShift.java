package com.sw3.reservation_microservice.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;

@Data
@Entity
@Table(name = "work_shifts")
public class WorkShift {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) - Comentado para permitir inserción manual de ID (espejo)
    private Long id;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // Usar String para compatibilidad entre microservicios

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(name = "barber_id", nullable = false)
    private String barberId; // Cambiado a String para coincidir con el microservicio de barberos
}
