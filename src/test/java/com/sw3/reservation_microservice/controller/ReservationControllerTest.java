package com.sw3.reservation_microservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw3.reservation_microservice.controller.dto.request.CreateReservationRequestDTO;
import com.sw3.reservation_microservice.domain.model.Reservation;
import com.sw3.reservation_microservice.service.facade.ReservationFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class) // 1. Cargamos solo el controlador
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula el cliente HTTP (Postman)

    @MockBean // 2. Simulamos el Facade (no ejecutamos lógica real)
    private ReservationFacade reservationFacade;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    // --- TEST 1: Crear Reserva (POST /) ---
    // --- TEST 1: Crear Reserva (CORREGIDO) ---
    @Test
    @DisplayName("POST / - Debería retornar 201 Created cuando se crea la reserva")
    void shouldCreateReservation() throws Exception {
        // ARRANGE
        CreateReservationRequestDTO request = new CreateReservationRequestDTO();
        request.setClientId("cliente1");
        request.setBarberId("barbero1");
        request.setServiceId(1L);
        // IMPORTANTE: Llenamos los campos que faltaban para evitar NullPointerException o errores de validación
        request.setStartTime(LocalDateTime.now().plusDays(1)); 
        request.setPrice(25.0);

        Reservation mockReservation = new Reservation();
        mockReservation.setId(100L);
        mockReservation.setClientId("cliente1");
        mockReservation.setStartTime(request.getStartTime()); // Coincidir fechas

        // Simulamos que el facade devuelve la reserva creada
        when(reservationFacade.createReservation(any(CreateReservationRequestDTO.class)))
                .thenReturn(mockReservation);

        // ACT & ASSERT
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Convertimos objeto a JSON
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print()) // <--- ESTO IMPRIMIRÁ EL ERROR REAL EN CONSOLA SI FALLA
                .andExpect(status().isCreated()) // Esperamos código 201
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.clientId").value("cliente1"));
    }

    // --- TEST 2: Obtener por ID (GET /cliente/reservations/{id}) ---
    @Test
    @DisplayName("GET /.../{id} - Debería retornar 200 OK si existe")
    void shouldGetReservationById() throws Exception {
        // ARRANGE
        Long resId = 1L;
        Reservation reservation = new Reservation();
        reservation.setId(resId);

        when(reservationFacade.findById(resId)).thenReturn(Optional.of(reservation));

        // ACT & ASSERT
        mockMvc.perform(get("/cliente/reservations/{id}", resId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resId));
    }

    @Test
    @DisplayName("GET /.../{id} - Debería retornar 404 Not Found si no existe")
    void shouldReturn404_WhenReservationNotFound() throws Exception {
        // ARRANGE
        when(reservationFacade.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        mockMvc.perform(get("/cliente/reservations/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // --- TEST 3: Cancelar Reserva (PUT con RequestParam) ---
    @Test
    @DisplayName("PUT /.../cancelar - Debería cancelar y retornar 200")
    void shouldCancelReservation() throws Exception {
        // ARRANGE
        Long resId = 1L;
        String clientId = "cli123";
        Reservation cancelledRes = new Reservation();
        cancelledRes.setId(resId);
        
        // Simulamos respuesta del facade
        when(reservationFacade.cancelReservation(resId, clientId)).thenReturn(cancelledRes);

        // ACT & ASSERT
        // Fíjate en el uso de .param() para simular ?clientId=...
        mockMvc.perform(put("/cliente/reservations/{id}/cancelar", resId)
                        .param("clientId", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resId));
    }

    // --- TEST 4: Listar Reservas Barbero (Manejo de Fechas) ---
    @Test
    @DisplayName("GET /barbero/... - Debería manejar correctamente las fechas en la URL")
    void shouldGetBarberReservations() throws Exception {
        // ARRANGE
        String barberId = "bar1";
        String dateStr = "2023-10-15T10:00:00"; // Formato ISO

        when(reservationFacade.getBarberSchedule(eq(barberId), any(LocalDateTime.class)))
                .thenReturn(List.of(new Reservation()));

        // ACT & ASSERT
        mockMvc.perform(get("/barbero/reservations/barbero/{barberId}/day", barberId)
                        .param("day", dateStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()); // Esperamos un array JSON
    }
    
    // --- TEST 5: Eliminar Reserva (DELETE) ---
    @Test
    @DisplayName("DELETE /... - Debería eliminar y retornar mensaje")
    void shouldDeleteReservation() throws Exception {
        // ARRANGE
        Long resId = 1L;
        String clientId = "cli1";
        
        doNothing().when(reservationFacade).deleteReservation(resId, clientId);

        // ACT & ASSERT
        mockMvc.perform(delete("/cliente/reservations/{id}", resId)
                        .param("clientId", clientId))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva eliminada exitosamente"));
    }
}