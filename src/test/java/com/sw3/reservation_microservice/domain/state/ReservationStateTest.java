package com.sw3.reservation_microservice.domain.state;

import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.CancellationNotAllowedException;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.InvalidReservationStateException;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.domain.model.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationStateTest {

    // --- TEST 1: Cancelación Exitosa (Con tiempo) ---
    @Test
    @DisplayName("Debe permitir cancelar si falta más de 1 hora para el inicio")
    void shouldCancel_WhenMoreThanOneHourLeft() {
        // Arrange
        Reservation reservation = new Reservation(); // Nace en EN_ESPERA
        // Ponemos la cita para dentro de 2 horas (tiempo suficiente)
        reservation.setStartTime(LocalDateTime.now().plusHours(2));

        // Act
        reservation.cancelar();

        // Assert
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELADA);
        assertThat(reservation.getState()).isInstanceOf(CanceladaState.class);
    }

    // --- TEST 2: Cancelación Rechazada (Muy tarde) ---
    @Test
    @DisplayName("Debe lanzar excepción si se intenta cancelar con menos de 1 hora de aviso")
    void shouldThrowException_WhenCancellingTooLate() {
        // Arrange
        Reservation reservation = new Reservation();
        // La cita es en 30 minutos (ya no se puede cancelar)
        reservation.setStartTime(LocalDateTime.now().plusMinutes(30));

        // Act & Assert
        assertThrows(CancellationNotAllowedException.class, () -> {
            reservation.cancelar();
        });

        // Verificamos que el estado NO cambió
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EN_ESPERA);
    }

    // --- TEST 3: Iniciar Servicio (Transición Válida) ---
    @Test
    @DisplayName("Debe transicionar correctamente de EN_ESPERA a EN_PROCESO")
    void shouldTransitionToEnProceso() {
        // Arrange
        Reservation reservation = new Reservation(); // EN_ESPERA

        // Act
        reservation.iniciarServicio();

        // Assert
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.EN_PROCESO);
        assertThat(reservation.getState()).isInstanceOf(EnProcesoState.class);
    }

    // --- TEST 4: Finalizar Servicio (Transición Inválida) ---
    @Test
    @DisplayName("No debe permitir finalizar una reserva que apenas está en espera")
    void shouldThrowException_WhenFinishingFromEnEspera() {
        // Arrange
        Reservation reservation = new Reservation(); // EN_ESPERA

        // Act & Assert
        // Lógica: No puedes finalizar si ni siquiera ha llegado el cliente
        assertThrows(InvalidReservationStateException.class, () -> {
            reservation.finalizarServicio();
        });
    }

    // --- TEST 5: Mecanismo de Hidratación (@PostLoad) ---
    @Test
    @DisplayName("Debe cargar el objeto State correcto basado en el Enum Status")
    void shouldLoadCorrectState_FromStatus() {
        // Arrange
        Reservation reservation = new Reservation();
        // Simulamos que Hibernate trajo este dato de la BD
        reservation.setStatus(ReservationStatus.FINALIZADA);

        // Act
        reservation.loadState(); // Método @PostLoad

        // Assert
        // Verificamos que el objeto 'state' sea compatible con el Enum
        assertThat(reservation.getState()).isInstanceOf(FinalizadaState.class);
    }
}