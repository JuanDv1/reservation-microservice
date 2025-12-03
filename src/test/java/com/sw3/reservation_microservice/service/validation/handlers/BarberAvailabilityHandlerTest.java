package com.sw3.reservation_microservice.service.validation.handlers;

import com.sw3.reservation_microservice.access.ReservationRepository;
import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationValidationException;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarberAvailabilityHandlerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private BarberAvailabilityHandler handler;

    // --- TEST 1: Barbero Disponible (Éxito) ---
    @Test
    @DisplayName("No debe lanzar excepción si el barbero está libre en ese horario")
    void shouldPass_WhenBarberIsAvailable() {
        // ARRANGE
        CreateReservationRequestDTO request = new CreateReservationRequestDTO();
        request.setBarberId("barber1");
        request.setServiceId(1L);
        request.setStartTime(LocalDateTime.now().plusHours(1));

        // Simulamos el servicio (duración 30 min)
        ServiceEntity service = new ServiceEntity();
        service.setDuration(30);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        // Simulamos que NO hay reservas solapadas (lista vacía)
        when(reservationRepository.findOverlappingReservations(
                eq("barber1"), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        // assertThatCode(...).doesNotThrowAnyException() verifica que el método corra hasta el final
        assertThatCode(() -> handler.validateConcrete(request))
                .doesNotThrowAnyException();
    }

    // --- TEST 2: Barbero Ocupado (Fallo) ---
    @Test
    @DisplayName("Debe lanzar excepción si el barbero ya tiene reserva en ese horario")
    void shouldThrowException_WhenBarberIsBusy() {
        // ARRANGE
        CreateReservationRequestDTO request = new CreateReservationRequestDTO();
        request.setBarberId("barber1");
        request.setServiceId(1L);
        request.setStartTime(LocalDateTime.now().plusHours(1));

        ServiceEntity service = new ServiceEntity();
        service.setDuration(30);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        // Simulamos que SÍ hay una reserva chocando
        List<Reservation> overlapping = List.of(new Reservation());
        when(reservationRepository.findOverlappingReservations(
                eq("barber1"), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(overlapping);

        // ACT & ASSERT
        assertThrows(ReservationValidationException.class, () -> {
            handler.validateConcrete(request);
        });
    }

    // --- TEST 3: Servicio No Existe (Fallo) ---
    @Test
    @DisplayName("Debe lanzar excepción si el servicio solicitado no existe")
    void shouldThrowException_WhenServiceNotFound() {
        // ARRANGE
        CreateReservationRequestDTO request = new CreateReservationRequestDTO();
        request.setBarberId("barber1");
        request.setServiceId(99L); // ID inexistente

        when(serviceRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        // Nota: El código lanza ReservationValidationException con mensaje "Servicio no encontrado"
        assertThrows(ReservationValidationException.class, () -> {
            handler.validateConcrete(request);
        });
    }

    // --- TEST 4: Barbero ID Inválido (Fallo Rápido) ---
    @Test
    @DisplayName("Debe lanzar excepción si el ID del barbero es nulo o vacío")
    void shouldThrowException_WhenBarberIdIsInvalid() {
        // ARRANGE
        CreateReservationRequestDTO request = new CreateReservationRequestDTO();
        request.setBarberId(""); // Vacío

        // ACT & ASSERT
        assertThrows(ReservationValidationException.class, () -> {
            handler.validateConcrete(request);
        });
    }
}