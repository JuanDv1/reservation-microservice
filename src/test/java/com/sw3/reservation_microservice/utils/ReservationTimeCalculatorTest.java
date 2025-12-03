package com.sw3.reservation_microservice.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationTimeCalculatorTest {

    // --- TEST 1: Cálculo Exacto (Múltiplo de 10) ---
    @Test
    @DisplayName("Debe calcular el tiempo exacto si la duración ya es múltiplo de 10")
    void shouldCalculateExactTime_WhenDurationIsMultipleOf10() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 10, 0); // 10:00 AM
        int duration = 30; // 30 mins

        // Act
        LocalDateTime result = ReservationTimeCalculator.calculateEndTime(start, duration);

        // Assert
        // 10:00 + 30 min = 10:30
        assertThat(result).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 30));
    }

    // --- TEST 2: Redondeo hacia arriba (Caso Clave) ---
    @Test
    @DisplayName("Debe redondear la duración al siguiente bloque de 10 minutos")
    void shouldRoundUpTime_WhenDurationIsNotMultipleOf10() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 10, 0);
        int duration = 35; // 35 mins -> Debería subir a 40 mins (4 bloques)

        // Act
        LocalDateTime result = ReservationTimeCalculator.calculateEndTime(start, duration);

        // Assert
        // 10:00 + 40 min (redondeado) = 10:40
        assertThat(result).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 40));
    }

    // --- TEST 3: Duración mínima ---
    @Test
    @DisplayName("Debe asignar al menos 1 bloque (10 min) para duraciones pequeñas")
    void shouldAssignMinimumBlock_ForSmallDurations() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 10, 0);
        int duration = 1; // 1 min -> Sube a 10 mins

        // Act
        LocalDateTime result = ReservationTimeCalculator.calculateEndTime(start, duration);

        // Assert
        assertThat(result).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 10));
    }

    // --- TEST 4: Cambio de Día (Edge Case) ---
    @Test
    @DisplayName("Debe manejar correctamente el cambio de día")
    void shouldHandleDayChange() {
        // Arrange
        // 1 de Octubre a las 23:50
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 23, 50);
        int duration = 20; // 20 mins -> Termina 00:10 del día siguiente

        // Act
        LocalDateTime result = ReservationTimeCalculator.calculateEndTime(start, duration);

        // Assert
        // Debe ser 2 de Octubre a las 00:10
        assertThat(result).isEqualTo(LocalDateTime.of(2023, 10, 2, 0, 10));
    }

    // --- TEST 5: Validaciones de Error ---
    @Test
    @DisplayName("Debe lanzar excepción con entradas inválidas")
    void shouldThrowException_WhenInputsAreInvalid() {
        LocalDateTime start = LocalDateTime.now();

        // 1. Fecha nula
        assertThrows(IllegalArgumentException.class, () -> 
            ReservationTimeCalculator.calculateEndTime(null, 30)
        );

        // 2. Duración cero
        assertThrows(IllegalArgumentException.class, () -> 
            ReservationTimeCalculator.calculateEndTime(start, 0)
        );

        // 3. Duración negativa
        assertThrows(IllegalArgumentException.class, () -> 
            ReservationTimeCalculator.calculateEndTime(start, -10)
        );
    }
    
    // --- TEST 6: Cálculo de Bloques (Helper) ---
    @Test
    @DisplayName("Debe calcular correctamente el número de bloques")
    void shouldCalculateBlocksCorrectly() {
        assertThat(ReservationTimeCalculator.calculateBlocksNeeded(10)).isEqualTo(1);
        assertThat(ReservationTimeCalculator.calculateBlocksNeeded(11)).isEqualTo(2);
        assertThat(ReservationTimeCalculator.calculateBlocksNeeded(29)).isEqualTo(3);
        assertThat(ReservationTimeCalculator.calculateBlocksNeeded(30)).isEqualTo(3);
    }
}