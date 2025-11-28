package com.sw3.reservation_microservice.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de solo lectura que representa la relación muchos a muchos
 * entre Barberos y Servicios.
 * Esta información se sincroniza desde los microservicios de barberos y servicios.
 * Se utiliza únicamente para validar que un barbero puede ofrecer un servicio específico
 * antes de crear una reserva.
 */
@Entity
@Table(name = "barber_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarberService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "barber_id", nullable = false)
    private Long barberId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    /**
     * Indica si el barbero está actualmente ofreciendo este servicio.
     * Permite activar/desactivar servicios específicos para cada barbero.
     */
    @Column(nullable = false)
    private Boolean active = true;
}
