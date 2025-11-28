package com.sw3.reservation_microservice.utils;

import java.time.LocalDateTime;

/**
 * Utilidad para calcular tiempos de reserva basados en bloques de 10 minutos.
 * Redondea la duración del servicio al siguiente múltiplo de 10.
 */
public class ReservationTimeCalculator {

    private static final int BLOCK_SIZE_MINUTES = 10;

    /**
     * Calcula la hora de fin de la reserva basado en la hora de inicio y la duración del servicio.
     * La duración se redondea hacia arriba al siguiente múltiplo de 10 minutos.
     * 
     * Ejemplo: Si el servicio dura 35 minutos, se asignarán 4 bloques (40 minutos).
     * 
     * @param startTime Hora de inicio de la reserva
     * @param serviceDurationMinutes Duración del servicio en minutos
     * @return Hora de fin calculada
     */
    public static LocalDateTime calculateEndTime(LocalDateTime startTime, int serviceDurationMinutes) {
        if (startTime == null) {
            throw new IllegalArgumentException("La hora de inicio no puede ser nula");
        }
        if (serviceDurationMinutes <= 0) {
            throw new IllegalArgumentException("La duración del servicio debe ser mayor a 0");
        }

        int blocksNeeded = calculateBlocksNeeded(serviceDurationMinutes);
        int totalMinutes = blocksNeeded * BLOCK_SIZE_MINUTES;
        
        return startTime.plusMinutes(totalMinutes);
    }

    /**
     * Calcula el número de bloques de 10 minutos necesarios para un servicio.
     * Redondea hacia arriba.
     * 
     * Ejemplos:
     * - 10 minutos = 1 bloque
     * - 15 minutos = 2 bloques (20 minutos total)
     * - 30 minutos = 3 bloques
     * - 35 minutos = 4 bloques (40 minutos total)
     * 
     * @param durationMinutes Duración del servicio en minutos
     * @return Número de bloques necesarios
     */
    public static int calculateBlocksNeeded(int durationMinutes) {
        return (int) Math.ceil((double) durationMinutes / BLOCK_SIZE_MINUTES);
    }

    /**
     * Calcula la duración total efectiva de la reserva en minutos,
     * considerando el redondeo a bloques de 10 minutos.
     * 
     * @param serviceDurationMinutes Duración del servicio en minutos
     * @return Duración total en bloques de 10 minutos
     */
    public static int calculateEffectiveDuration(int serviceDurationMinutes) {
        return calculateBlocksNeeded(serviceDurationMinutes) * BLOCK_SIZE_MINUTES;
    }
}
