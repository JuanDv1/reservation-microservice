package com.sw3.reservation_microservice.service.facade;

import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.dto.request.RescheduleReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PATRÓN FACHADA: Proporciona una interfaz simplificada para las operaciones de reservas.
 * Coordina múltiples subsistemas: validación, persistencia, lógica de negocio y estado.
 */
@Component
public class ReservationFacade {

    @Autowired
    private ReservationService reservationService;

    /**
     * Crea una nueva reserva coordinando:
     * - Cadena de responsabilidades (validaciones)
     * - Patrón State (estado inicial)
     * - Persistencia
     */
    public Reservation createReservation(CreateReservationRequestDTO request) {
        return reservationService.createReservation(request);
    }

    /**
     * Cancela una reserva coordinando:
     * - Patrón State (validación de transición)
     * - Validación de tiempo (1 hora antes)
     * - Persistencia
     */
    public Reservation cancelReservation(Long reservationId, String clientId) {
        return reservationService.cancelReservation(reservationId, clientId);
    }

    /**
     * Cambia el estado de la reserva coordinando:
     * - Validación automática de inasistencia (10 minutos después del inicio)
     * - Patrón State (transiciones válidas)
     * - Persistencia
     */
    public Reservation changeReservationStatus(Long reservationId, String newStatus) {
        return reservationService.changeReservationStatus(reservationId, newStatus);
    }

    /**
     * Consulta simplificada: Obtener reserva por ID
     */
    public Optional<Reservation> findById(Long id) {
        return reservationService.getReservationById(id);
    }

    /**
     * Consulta simplificada: Obtener todas las reservas
     */
    public List<Reservation> findAll() {
        return reservationService.getAllReservations();
    }

    /**
     * Consulta simplificada: Reservas activas del cliente
     */
    public List<Reservation> getClientActiveReservations(String clientId) {
        return reservationService.getActiveReservationsByClient(clientId);
    }

    /**
     * Consulta simplificada: Historial del cliente
     */
    public List<Reservation> getClientHistory(String clientId) {
        return reservationService.getReservationHistoryByClient(clientId);
    }

    /**
     * Consulta simplificada: Todas las reservas de un barbero
     */
    public List<Reservation> getBarberReservations(String barberId) {
        return reservationService.getAllReservationsByBarber(barberId);
    }

    /**
     * Consulta simplificada: Agenda del barbero
     */
    public List<Reservation> getBarberSchedule(String barberId, LocalDateTime day) {
        return reservationService.getBarberReservationsByDay(barberId, day);
    }

    /**
     * Validación de regla de negocio: ¿Se puede desactivar un barbero?
     */
    public boolean canDesactivateBarber(String barberId) {
        return reservationService.canDesactivateBarber(barberId);
    }

    /**
     * Validación de regla de negocio: ¿Se puede desactivar un servicio?
     */
    public boolean canDesactivateService(Long serviceId) {
        return reservationService.canDesactivateService(serviceId);
    }

    /**
     * Reprograma una reserva coordinando:
     * - Cadena de validaciones de reprogramación
     * - Actualización de fechas
     * - Persistencia
     */
    public Reservation rescheduleReservation(Long reservationId, String clientId, RescheduleReservationRequestDTO request) {
        return reservationService.rescheduleReservation(reservationId, clientId, request);
    }

    /**
     * Elimina una reserva coordinando:
     * - Validación de pertenencia al cliente
     * - Validación de estado (solo canceladas o finalizadas)
     * - Eliminación permanente
     */
    public void deleteReservation(Long reservationId, String clientId) {
        reservationService.deleteReservation(reservationId, clientId);
    }
}
