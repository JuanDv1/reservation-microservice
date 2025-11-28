package com.sw3.reservation_microservice.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad Barbero - Copia simplificada sincronizada del microservicio de barberos.
 * Contiene SOLO los datos necesarios para el contexto de reservas.
 * Se mantiene actualizada mediante eventos de RabbitMQ.
 */
@Entity
@Table(name = "barber")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Barber {

    @Id
    @Column(name = "barber_id", nullable = false)
    private String barberId;

    /**
     * Nombre completo del barbero (para mostrar en la reserva).
     */
    @Column(nullable = false)
    private String name;

    /**
     * IDs de servicios que ofrece este barbero (almacenado como CSV).
     * Usado para validar que el barbero puede realizar el servicio solicitado.
     * Ejemplo: "101,102,107,201,202"
     */
    @Column(name = "service_ids", columnDefinition = "TEXT")
    private String serviceIds;

    /**
     * Estado de disponibilidad del barbero para aceptar reservas.
     * Valores: "Disponible", "No Disponible"
     */
    @Column(name = "availability_status", nullable = false)
    private String availabilityStatus;

    /**
     * Estado del registro en el sistema (soft delete).
     * Valores: "Activo", "Inactivo"
     */
    @Column(name = "system_status", nullable = false)
    private String systemStatus;

    /**
     * Campo booleano derivado para compatibilidad.
     * true = Activo, false = Inactivo
     */
    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        syncActiveField();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        syncActiveField();
    }

    /**
     * Sincroniza el campo active con systemStatus.
     */
    private void syncActiveField() {
        if (systemStatus != null) {
            active = "Activo".equals(systemStatus);
        }
    }

    /**
     * Verifica si el barbero está activo en el sistema.
     */
    public boolean isActive() {
        return "Activo".equals(systemStatus);
    }

    /**
     * Verifica si el barbero está disponible para recibir reservas.
     * Debe estar activo Y disponible.
     */
    public boolean isAvailableForReservations() {
        return "Disponible".equals(availabilityStatus) && isActive();
    }

    /**
     * Verifica si el barbero puede realizar un servicio específico.
     */
    public boolean canProvideService(Long serviceId) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return false;
        }
        return serviceIds.contains(serviceId.toString());
    }

    /**
     * Para backward compatibility con código existente.
     * @deprecated Usar isActive() o isAvailableForReservations()
     */
    @Deprecated
    public Boolean getActive() {
        return active;
    }
}
