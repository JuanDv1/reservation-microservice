package com.sw3.reservation_microservice.access;

import com.sw3.reservation_microservice.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * [RF14] CRÍTICO: Busca reservas que se solapen con un nuevo intervalo de tiempo para un barbero específico.
     * Esencial para evitar dobles reservas.
     */
    @Query("SELECT r FROM Reservation r WHERE r.barberId = :barberId AND r.startTime < :end AND r.endTime > :start AND r.status <> 'CANCELADA'")
    List<Reservation> findOverlappingReservations(@Param("barberId") String barberId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * [RF15] Busca las reservas activas (futuras) de un cliente, ordenadas por la más próxima.
     */
    List<Reservation> findByClientIdAndStartTimeAfterOrderByStartTimeAsc(String clientId, LocalDateTime currentTime);

    /**
     * [RF15] Busca el historial de reservas (pasadas) de un cliente, ordenadas por la más reciente.
     */
    List<Reservation> findByClientIdAndStartTimeBeforeOrderByStartTimeDesc(String clientId, LocalDateTime currentTime);

    /**
     * [RF18] Busca todas las reservas de un barbero para un día específico, ordenadas por hora de inicio.
     */
    List<Reservation> findByBarberIdAndStartTimeBetweenOrderByStartTimeAsc(String barberId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    /**
     * Obtiene todas las reservas activas (no canceladas) de un barbero, ordenadas por hora de inicio.
     * Usado para calcular disponibilidad en el frontend.
     */
    @Query("SELECT r FROM Reservation r WHERE r.barberId = :barberId AND r.status <> 'CANCELADA' AND r.startTime >= :currentTime ORDER BY r.startTime ASC")
    List<Reservation> findActiveReservationsByBarberId(@Param("barberId") String barberId, @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * [RF16] Busca una reserva específica por su ID y el ID del cliente que la posee.
     * Para seguridad, asegura que un cliente solo pueda ver/cancelar sus propias reservas.
     */
    Optional<Reservation> findByIdAndClientId(Long reservationId, String clientId);

    /**
     * [RF06] Verifica de forma eficiente si un barbero tiene reservas futuras activas.
     * Se usa antes de poder inactivar un barbero.
     */
    boolean existsByBarberIdAndStartTimeAfter(String barberId, LocalDateTime currentTime);
    
    /**
     * [RF11] Verifica de forma eficiente si un servicio está asociado a alguna reserva futura activa.
     * Se usa antes de poder inactivar un servicio.
     */
    boolean existsByServiceIdAndStartTimeAfter(Long serviceId, LocalDateTime currentTime);
}