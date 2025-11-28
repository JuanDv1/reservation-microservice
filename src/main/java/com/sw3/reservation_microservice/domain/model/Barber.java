package com.sw3.reservation_microservice.domain.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad de solo lectura que representa un barbero.
 * Esta información se sincroniza desde el microservicio de barberos.
 * Se utiliza únicamente para validaciones en la creación de reservas.
 * Los datos completos del barbero se obtienen desde su microservicio original.
 */
@Data
@Entity
@Table(name = "barbers")
public class Barber {
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Boolean availabilityStatus;
}
