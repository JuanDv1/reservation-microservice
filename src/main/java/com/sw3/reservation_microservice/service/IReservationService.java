package com.sw3.reservation_microservice.service;

import com.sw3.reservation_microservice.controller.dto.request.*;
import com.sw3.reservation_microservice.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define el contrato del servicio de gestión de reservas.
 */
public interface IReservationService {

    /**
     * Crea una nueva reserva después de validarla con la cadena de responsabilidades.
     * 
     * @param request DTO con los datos de la reserva a crear
     * @return la reserva creada
     */
    Reservation createReservation(CreateReservationRequestDTO request);

    /**
     * Obtiene una reserva por su ID.
     * 
     * @param id identificador de la reserva
     * @return Optional con la reserva si existe
     */
    Optional<Reservation> getReservationById(Long id);

    /**
     * Obtiene todas las reservas del sistema.
     * 
     * @return lista de todas las reservas
     */
    List<Reservation> getAllReservations();

    /**
     * Obtiene todas las reservas activas (futuras) de un cliente.
     * 
     * @param clientId identificador del cliente
     * @return lista de reservas futuras del cliente
     */
    List<Reservation> getActiveReservationsByClient(String clientId);

    /**
     * Obtiene el historial de reservas (pasadas) de un cliente.
     * 
     * @param clientId identificador del cliente
     * @return lista de reservas pasadas del cliente
     */
    List<Reservation> getReservationHistoryByClient(String clientId);

    /**
     * Obtiene las reservas de un barbero para un día específico.
     * 
     * @param barberId identificador del barbero
     * @param day fecha del día a consultar
     * @return lista de reservas del barbero en el día especificado
     */
    List<Reservation> getBarberReservationsByDay(String barberId, LocalDateTime day);

    /**
     * Cancela una reserva (usa el patrón State).
     * 
     * @param reservationId identificador de la reserva
     * @param clientId identificador del cliente
     * @return la reserva cancelada
     */
    Reservation cancelReservation(Long reservationId, String clientId);

    /**
     * Inicia el servicio de una reserva (usa el patrón State).
     * 
     * @param reservationId identificador de la reserva
     * @return la reserva con el servicio iniciado
     */
    Reservation startService(Long reservationId);

    /**
     * Finaliza el servicio de una reserva (usa el patrón State).
     * 
     * @param reservationId identificador de la reserva
     * @return la reserva con el servicio finalizado
     */
    Reservation finishService(Long reservationId);

    /**
     * Verifica si un barbero puede ser desactivado (no tiene reservas futuras).
     * 
     * @param barberId identificador del barbero
     * @return true si el barbero puede ser desactivado, false en caso contrario
     */
    boolean canDeactivateBarber(String barberId);

    /**
     * Verifica si un servicio puede ser desactivado (no está en reservas futuras).
     * 
     * @param serviceId identificador del servicio
     * @return true si el servicio puede ser desactivado, false en caso contrario
     */
    boolean canDeactivateService(Long serviceId);

    /**
     * Reprograma una reserva (cambia fecha/hora).
     * 
     * @param reservationId identificador de la reserva
     * @param clientId identificador del cliente
     * @param request DTO con los nuevos datos de fecha/hora
     * @return la reserva reprogramada
     */
    Reservation rescheduleReservation(Long reservationId, String clientId, RescheduleReservationRequestDTO request);

    /**
     * Elimina una reserva permanentemente.
     * Solo permite eliminar reservas canceladas o finalizadas.
     * 
     * @param reservationId identificador de la reserva
     * @param clientId identificador del cliente
     */
    void deleteReservation(Long reservationId, String clientId);
}
