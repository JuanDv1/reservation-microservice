package com.sw3.reservation_microservice.controller;

import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.controller.dto.request.RescheduleReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.service.facade.ReservationFacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ReservationController {

    @Autowired
    private ReservationFacade reservationFacade;

    /**
     * Obtiene todas las reservas.
     */
    @GetMapping("cliente/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationFacade.findAll();
        return ResponseEntity.ok(reservations);
    }

    /**
     * Crea una nueva reserva.
     */
    @PostMapping("/")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody CreateReservationRequestDTO request) {
        Reservation reservation = reservationFacade.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    /**
     * Obtiene una reserva por ID.
     */
    @GetMapping("cliente/reservations/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationFacade.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene las reservas activas (futuras) de un cliente.
     */
    @GetMapping("cliente/reservations/cliente/{clientId}/active")
    public ResponseEntity<List<Reservation>> getActiveReservations(@PathVariable String clientId) {
        List<Reservation> reservations = reservationFacade.getClientActiveReservations(clientId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtiene el historial de reservas (pasadas) de un cliente.
     */
    @GetMapping("cliente/reservations/cliente/{clientId}/history")
    public ResponseEntity<List<Reservation>> getReservationHistory(@PathVariable String clientId) {
        List<Reservation> reservations = reservationFacade.getClientHistory(clientId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Obtiene las reservas de un barbero para un día específico.
     */
    @GetMapping("barbero/reservations/barbero/{barberId}/day")
    public ResponseEntity<List<Reservation>> getBarberReservationsByDay(
            @PathVariable String barberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime day) {
        List<Reservation> reservations = reservationFacade.getBarberSchedule(barberId, day);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Cancela una reserva (validando que sea del cliente y que cumpla las reglas).
     */
    @PutMapping("cliente/reservations/{id}/cancelar")
    public ResponseEntity<Reservation> cancelReservation(
            @PathVariable Long id,
            @RequestParam String clientId) {
        Reservation reservation = reservationFacade.cancelReservation(id, clientId);
        return ResponseEntity.ok(reservation);
    }


    /**
     * Cambia el estado de una reserva (para barberos).
     * Permite transiciones: EN_ESPERA -> EN_PROCESO -> FINALIZADA
     * También valida automáticamente si debe marcarse como INASISTENCIA.
     */
    @PutMapping("barbero/reservations/{id}/estado")
    public ResponseEntity<Reservation> changeReservationStatus(
            @PathVariable Long id,
            @RequestParam String newStatus) {
        Reservation reservation = reservationFacade.changeReservationStatus(id, newStatus);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Verifica si un barbero puede ser desactivado.
     */
    @GetMapping("admin/reservations/barber/{barberId}/can-desactivate")
    public ResponseEntity<Boolean> canDesactivateBarber(@PathVariable String barberId) {
        boolean canDesactivate = reservationFacade.canDesactivateBarber(barberId);
        return ResponseEntity.ok(canDesactivate);
    }

    /**
     * Verifica si un servicio puede ser desactivado.
     */
    @GetMapping("admin/reservations/service/{serviceId}/can-desactivate")
    public ResponseEntity<Boolean> canDesactivateService(@PathVariable Long serviceId) {
        boolean canDesactivate = reservationFacade.canDesactivateService(serviceId);
        return ResponseEntity.ok(canDesactivate);
    }

    /**
     * Reprograma una reserva (cambia fecha/hora).
     */
    @PutMapping("cliente/reservations/{id}/reprogramar")
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
    @DeleteMapping("cliente/reservations/{id}")
    public ResponseEntity<String> deleteReservation(
            @PathVariable Long id,
            @RequestParam String clientId) {
        reservationFacade.deleteReservation(id, clientId);
        return ResponseEntity.ok("Reserva eliminada exitosamente");
    }
}
