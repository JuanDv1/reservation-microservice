package com.sw3.reservation_microservice.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Turno de trabajo de un barbero (mañana/tarde).
 * Ejemplo: 08:00 - 12:00 o 14:00 - 18:00
 */
@Entity
@Table(name = "barber_shift")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarberShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private BarberSchedule schedule;

    public BarberShift(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
