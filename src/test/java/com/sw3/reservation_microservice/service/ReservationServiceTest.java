package com.sw3.reservation_microservice.service;

import com.sw3.reservation_microservice.access.ReservationRepository;
import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.InvalidReservationDeletionException;
import com.sw3.reservation_microservice.config.controladorExcepciones.excepcionesPropias.ReservationNotFoundException;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.domain.model.ReservationStatus;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import com.sw3.reservation_microservice.domain.state.EnEsperaState;
import com.sw3.reservation_microservice.service.validation.ReservationValidatorChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 1. Habilita Mockito
class ReservationServiceTest {

    // Mocks: Simulan el comportamiento de las dependencias externas
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationValidatorChain validatorChain;

    @Mock
    private ServiceRepository serviceRepository;

    // InjectMocks: Crea la instancia real del servicio e inyecta los Mocks definidos arriba
    @InjectMocks
    private ReservationService reservationService;

    // --- TEST 1: Crear Reserva Exitosamente ---
    @Test
    @DisplayName("Debe crear una reserva correctamente cuando los datos son válidos")
    void shouldCreateReservation_WhenDataIsValid() {
        // ARRANGE (Preparar)
        CreateReservationRequestDTO request = new CreateReservationRequestDTO();
        request.setServiceId(1L);
        request.setStartTime(LocalDateTime.of(2023, 10, 10, 10, 0));
        request.setClientId("cliente123");
        request.setPrice(20.0);

        ServiceEntity mockService = new ServiceEntity();
        mockService.setDuration(60); // Duración de 60 mins

        // Cuando busquen el servicio ID 1, devuelve nuestro mockService
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(mockService));
        
        // Cuando guarden cualquier reserva, devuélvela (simulamos que la BD la guardó)
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT (Actuar)
        Reservation result = reservationService.createReservation(request);

        // ASSERT (Verificar)
        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("cliente123");
        // Verificar que calculó el tiempo final (10:00 + 60min = 11:00)
        assertThat(result.getEndTime()).isEqualTo(LocalDateTime.of(2023, 10, 10, 11, 0));
        
        // Verificar que se llamó a la validación
        verify(validatorChain).validate(request);
    }

    // --- TEST 2: Lógica de Inasistencia Automática ---
    @Test
    @DisplayName("Debe marcar como INASISTENCIA si se intenta cambiar estado pasados 10 mins")
    void shouldMarkAsInasistencia_WhenLateArrival() {
        // ARRANGE
        Long resId = 1L;
        Reservation reservation = new Reservation();
        reservation.setId(resId);
        reservation.setStatus(ReservationStatus.EN_ESPERA);
        // Hora de inicio fue hace 20 minutos (ya pasaron los 10 de tolerancia)
        reservation.setStartTime(LocalDateTime.now().minusMinutes(20));

        when(reservationRepository.findById(resId)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        // Intentamos iniciar el servicio ("EN_PROCESO"), pero llegamos tarde
        Reservation result = reservationService.changeReservationStatus(resId, "EN_PROCESO");

        // ASSERT
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.INASISTENCIA);
        // Verificamos que NO se llamó a 'iniciarServicio' (lógica interna del estado)
        // Nota: Esto depende de cómo tengas implementado el State, pero verificamos el resultado final.
    }

    // --- TEST 3: Cancelar Reserva ---
    // --- TEST 3: Cancelar Reserva (CORREGIDO) ---
    @Test
    @DisplayName("Debe cancelar la reserva correctamente")
    void shouldCancelReservation() {
        // ARRANGE
        Long resId = 1L;
        String clientId = "cli1";
        
        Reservation reservation = new Reservation(); 
        reservation.setStatus(ReservationStatus.EN_ESPERA);
        reservation.setState(new EnEsperaState()); 
        
        // CORRECCIÓN: Asignamos una fecha futura.
        // Ponemos 2 días en el futuro para asegurar que no viole reglas de "cancelación tardía"
        reservation.setStartTime(LocalDateTime.now().plusDays(2)); 

        when(reservationRepository.findByIdAndClientId(resId, clientId))
                .thenReturn(Optional.of(reservation));
        
        // Cuando guarde, devuelve el mismo objeto modificado
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        Reservation result = reservationService.cancelReservation(resId, clientId);

        // ASSERT
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCELADA);
        verify(reservationRepository).save(reservation);
    }

    // --- TEST 4: Excepción al Eliminar ---
    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar una reserva activa (EN_ESPERA)")
    void shouldThrowException_WhenDeletingActiveReservation() {
        // ARRANGE
        Long resId = 1L;
        String clientId = "cli1";
        
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.EN_ESPERA); // No se puede borrar si está en espera

        when(reservationRepository.findByIdAndClientId(resId, clientId))
                .thenReturn(Optional.of(reservation));

        // ACT & ASSERT
        // Esperamos que lance tu excepción personalizada
        assertThrows(InvalidReservationDeletionException.class, () -> {
            reservationService.deleteReservation(resId, clientId);
        });

        // Aseguramos que NUNCA se llamó al delete de la base de datos
        verify(reservationRepository, never()).delete(any());
    }
    
    // --- TEST 5: Reserva no encontrada ---
    @Test
    @DisplayName("Debe lanzar excepción si no encuentra la reserva para eliminar")
    void shouldThrowException_WhenReservationNotFound() {
        // ARRANGE
        when(reservationRepository.findByIdAndClientId(99L, "cli1"))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(ReservationNotFoundException.class, () -> {
            reservationService.deleteReservation(99L, "cli1");
        });
    }
}