package com.sw3.reservation_microservice.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Horario semanal de un barbero.
 * Representa un día de la semana con sus turnos.
 * dayOfWeek: 0=Domingo, 1=Lunes, ..., 6=Sábado
 */
@Entity
@Table(name = "barber_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarberSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 0=Domingo, 1=Lunes, ..., 6=Sábado

    @ManyToOne
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BarberShift> shifts = new ArrayList<>();

    public BarberSchedule(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.shifts = new ArrayList<>();
    }

    public void addShift(BarberShift shift) {
        shifts.add(shift);
        shift.setSchedule(this);
    }
}
