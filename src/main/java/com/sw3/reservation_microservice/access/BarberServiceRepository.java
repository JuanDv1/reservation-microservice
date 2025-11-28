package com.sw3.reservation_microservice.access;

import com.sw3.reservation_microservice.domain.model.BarberService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de solo lectura para validar la relación entre Barberos y Servicios.
 * Los datos se sincronizan desde otros microservicios.
 */
@Repository
public interface BarberServiceRepository extends JpaRepository<BarberService, Long> {

    /**
     * Busca todos los servicios que ofrece un barbero específico.
     */
    List<BarberService> findByBarberId(Long barberId);

    /**
     * Busca todos los servicios activos que ofrece un barbero.
     */
    List<BarberService> findByBarberIdAndActiveTrue(Long barberId);

    /**
     * Busca todos los barberos que ofrecen un servicio específico.
     */
    List<BarberService> findByServiceId(Long serviceId);

    /**
     * Busca todos los barberos activos que ofrecen un servicio.
     */
    List<BarberService> findByServiceIdAndActiveTrue(Long serviceId);

    /**
     * Verifica si existe una relación activa entre un barbero y un servicio.
     * Esta validación es crucial antes de crear una reserva.
     */
    boolean existsByBarberIdAndServiceIdAndActiveTrue(Long barberId, Long serviceId);

    /**
     * Busca una relación específica entre barbero y servicio.
     */
    Optional<BarberService> findByBarberIdAndServiceId(Long barberId, Long serviceId);

    /**
     * Busca los IDs de barberos que ofrecen un servicio y están disponibles.
     * Útil para validar disponibilidad antes de crear una reserva.
     */
    @Query("SELECT bs.barberId FROM BarberService bs " +
           "JOIN Barber b ON bs.barberId = b.id " +
           "WHERE bs.serviceId = :serviceId " +
           "AND bs.active = true " +
           "AND b.availabilityStatus = true")
    List<Long> findAvailableBarberIdsByServiceId(@Param("serviceId") Long serviceId);
}
