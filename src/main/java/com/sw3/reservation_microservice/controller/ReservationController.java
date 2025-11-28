package com.sw3.reservation_microservice.controller;

import com.sw3.reservation_microservice.controller.dto.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.dto.RescheduleReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.facade.ReservationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationFacade reservationFacade;

    /**
     * Obtiene todas las reservas.
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationFacade.findAll();
        return ResponseEntity.ok(reservations);
    }

    /**
     * Crea una nueva reserva.
     */
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody CreateReservationRequestDTO request) {
        Reservation reservation = reservationFacade.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    /**
     * Obtiene una reserva por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationFacade.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene las reservas activas (futuras) de un cliente.
     */
    @GetMapping("/client/{clientId}/active")
    public ResponseEntity<List<Reservation>> getActiveReservations(@PathVariable String clientId) {
        List<Reservation> reservations = reservationFacade.getClientActiveReservations(clientId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtiene el historial de reservas (pasadas) de un cliente.
     */
    @GetMapping("/client/{clientId}/history")
    public ResponseEntity<List<Reservation>> getReservationHistory(@PathVariable String clientId) {
        List<Reservation> reservations = reservationFacade.getClientHistory(clientId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtiene las reservas de un barbero para un día específico.
     */
    @GetMapping("/barber/{barberId}/day")
    public ResponseEntity<List<Reservation>> getBarberReservationsByDay(
            @PathVariable String barberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime day) {
        List<Reservation> reservations = reservationFacade.getBarberSchedule(barberId, day);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Cancela una reserva (validando que sea del cliente y que cumpla las reglas).
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancelReservation(
            @PathVariable Long id,
            @RequestParam String clientId) {
        Reservation reservation = reservationFacade.cancelReservation(id, clientId);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Inicia el servicio de una reserva.
     */
    @PutMapping("/{id}/start")
    public ResponseEntity<Reservation> startService(@PathVariable Long id) {
        Reservation reservation = reservationFacade.startService(id);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Finaliza el servicio de una reserva.
     */
    @PutMapping("/{id}/finish")
    public ResponseEntity<Reservation> finishService(@PathVariable Long id) {
        Reservation reservation = reservationFacade.finishService(id);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Verifica si un barbero puede ser desactivado.
     */
    @GetMapping("/barber/{barberId}/can-desactivate")
    public ResponseEntity<Boolean> canDeactivateBarber(@PathVariable String barberId) {
        boolean canDeactivate = reservationFacade.canDeactivateBarber(barberId);
        return ResponseEntity.ok(canDeactivate);
    }

    /**
     * Verifica si un servicio puede ser desactivado.
     */
    @GetMapping("/service/{serviceId}/can-desactivate")
    public ResponseEntity<Boolean> canDeactivateService(@PathVariable Long serviceId) {
        boolean canDeactivate = reservationFacade.canDeactivateService(serviceId);
        return ResponseEntity.ok(canDeactivate);
    }

    /**
     * Reprograma una reserva (cambia fecha/hora).
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Reservation> rescheduleReservation(
            @PathVariable Long id,
            @RequestParam String clientId,
            @Valid @RequestBody RescheduleReservationRequestDTO request) {
        Reservation reservation = reservationFacade.rescheduleReservation(id, clientId, request);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Elimina una reserva permanentemente.
     * Solo permite eliminar reservas canceladas o finalizadas.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(
            @PathVariable Long id,
            @RequestParam String clientId) {
        reservationFacade.deleteReservation(id, clientId);
        return ResponseEntity.ok("Reserva eliminada exitosamente");
    }
}
