package com.sw3.reservation_microservice.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de solo lectura que representa un servicio.
 * Esta información se sincroniza desde el microservicio de servicios.
 * Se utiliza únicamente para validaciones en la creación de reservas.
 * Los datos completos del servicio se obtienen desde su microservicio original.
 */
@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceEntity {

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Boolean availabilityStatus;
}
