package com.sw3.reservation_microservice.service;

import com.sw3.reservation_microservice.access.ReservationRepository;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.InvalidReservationDeletionException;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationNotFoundException;
import com.sw3.reservation_microservice.controller.dto.request.*;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.domain.model.ReservationStatus;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import com.sw3.reservation_microservice.service.validation.ReservationValidatorChain;
import com.sw3.reservation_microservice.service.validation.RescheduleValidatorChain;
import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.utils.ReservationTimeCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar reservas.
 */
@Service
public class ReservationService implements IReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationValidatorChain validatorChain;

    @Autowired
    private RescheduleValidatorChain rescheduleValidatorChain;

    @Autowired
    private ServiceRepository serviceRepository;

    /**
     * Crea una nueva reserva después de validarla con la cadena de responsabilidades.
     */
    @Transactional
    public Reservation createReservation(CreateReservationRequestDTO request) {
        // Ejecutar todas las validaciones
        validatorChain.validate(request);

        // Obtener el servicio para calcular la duración
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado."));

        // Calcular automáticamente el endTime basado en bloques de 10 minutos
        LocalDateTime expectedEndTime = ReservationTimeCalculator.calculateEndTime(
            request.getStartTime(), 
            service.getDuration()
        );

        // Si todas las validaciones pasan, crear la reserva
        Reservation reservation = new Reservation();
        reservation.setClientId(request.getClientId());
        reservation.setBarberId(request.getBarberId());
        reservation.setServiceId(request.getServiceId());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(expectedEndTime);
        reservation.setPrice(request.getPrice());
        // El status y state ya se inicializan en el constructor

        return reservationRepository.save(reservation);
    }

    /**
     * Obtiene una reserva por su ID.
     */
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    /**
     * Obtiene todas las reservas del sistema.
     */
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Obtiene todas las reservas de un cliente (futuras).
     */
    public List<Reservation> getActiveReservationsByClient(String clientId) {
        return reservationRepository.findByClientIdAndStartTimeAfterOrderByStartTimeAsc(
            clientId, 
            LocalDateTime.now()
        );
    }

    /**
     * Obtiene el historial de reservas de un cliente (pasadas).
     */
    public List<Reservation> getReservationHistoryByClient(String clientId) {
        return reservationRepository.findByClientIdAndStartTimeBeforeOrderByStartTimeDesc(
            clientId, 
            LocalDateTime.now()
        );
    }

    /**
     * Obtiene las reservas de un barbero para un día específico.
     */
    public List<Reservation> getBarberReservationsByDay(String barberId, LocalDateTime day) {
        LocalDateTime startOfDay = day.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = day.toLocalDate().atTime(23, 59, 59);
        
        return reservationRepository.findByBarberIdAndStartTimeBetweenOrderByStartTimeAsc(
            barberId, 
            startOfDay, 
            endOfDay
        );
    }

    /**
     * Cancela una reserva (usa el patrón State).
     */
    @Transactional
    public Reservation cancelReservation(Long reservationId, String clientId) {
        Reservation reservation = reservationRepository.findByIdAndClientId(reservationId, clientId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada o no pertenece al cliente."));

        // El patrón State maneja la lógica de cancelación y validaciones
        reservation.cancelar();

        return reservationRepository.save(reservation);
    }

    /**
     * Inicia el servicio de una reserva (usa el patrón State).
     */
    @Transactional
    public Reservation startService(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada."));

        reservation.iniciarServicio();

        return reservationRepository.save(reservation);
    }

    /**
     * Finaliza el servicio de una reserva (usa el patrón State).
     */
    @Transactional
    public Reservation finishService(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada."));

        reservation.finalizarServicio();

        return reservationRepository.save(reservation);
    }

    /**
     * Verifica si un barbero puede ser desactivado (no tiene reservas futuras).
     */
    public boolean canDeactivateBarber(String barberId) {
        return !reservationRepository.existsByBarberIdAndStartTimeAfter(barberId, LocalDateTime.now());
    }

    /**
     * Verifica si un servicio puede ser desactivado (no está en reservas futuras).
     */
    public boolean canDeactivateService(Long serviceId) {
        return !reservationRepository.existsByServiceIdAndStartTimeAfter(serviceId, LocalDateTime.now());
    }

    /**
     * Reprograma una reserva (cambia fecha/hora).
     */
    @Transactional
    public Reservation rescheduleReservation(Long reservationId, String clientId, RescheduleReservationRequestDTO request) {
        // Obtener la reserva
        Reservation reservation = reservationRepository.findByIdAndClientId(reservationId, clientId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada o no pertenece al cliente."));

        // Validar que la reserva esté en estado EN_ESPERA
        if (!"EN_ESPERA".equals(reservation.getStatus().name())) {
            throw new RuntimeException("Solo se pueden reprogramar reservas en estado EN_ESPERA.");
        }

        // Ejecutar validaciones de reprogramación
        rescheduleValidatorChain.validate(request, reservation.getBarberId(), reservationId);

        // Actualizar fechas
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());

        return reservationRepository.save(reservation);
    }

    /**
     * Elimina una reserva permanentemente.
     * Solo permite eliminar reservas canceladas o finalizadas.
     */
    @Transactional
    public void deleteReservation(Long reservationId, String clientId) {
        // Obtener la reserva
        Reservation reservation = reservationRepository.findByIdAndClientId(reservationId, clientId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId, clientId));

        // Validar que la reserva esté en un estado eliminable
        if (reservation.getStatus() != ReservationStatus.CANCELADA && 
            reservation.getStatus() != ReservationStatus.FINALIZADA) {
            throw new InvalidReservationDeletionException(reservation.getStatus());
        }

        // Eliminar la reserva
        reservationRepository.delete(reservation);
    }
}
