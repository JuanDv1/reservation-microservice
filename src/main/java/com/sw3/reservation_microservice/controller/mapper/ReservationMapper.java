package com.sw3.reservation_microservice.controller.mapper;

import com.sw3.reservation_microservice.controller.dto.response.ReservationListItemDTO;
import com.sw3.reservation_microservice.controller.dto.response.ReservationResponseDTO;
import com.sw3.reservation_microservice.domain.model.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entidades Reservation a DTOs de respuesta.
 * Separa la capa de dominio de la capa de presentación.
 * Estrategia: Solo retorna IDs, el frontend compone los objetos completos.
 */
@Component
public class ReservationMapper {

    /**
     * Convierte una Reservation a ReservationResponseDTO.
     * Solo incluye IDs, sin objetos anidados.
     *
     * @param reservation La entidad Reservation
     * @return DTO con IDs únicamente
     */
    public ReservationResponseDTO toResponseDTO(Reservation reservation) {
        if (reservation == null) return null;

        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setClientId(reservation.getClientId());
        dto.setBarberId(reservation.getBarberId());
        dto.setServiceId(reservation.getServiceId());
        dto.setStart(reservation.getStartTime());
        dto.setEnd(reservation.getEndTime());
        dto.setPrice(reservation.getPrice());
        dto.setStatus(mapStatus(reservation.getStatus()));

        return dto;
    }

    /**
     * Convierte una Reservation a ReservationListItemDTO.
     * Optimizado para listas (sin objetos anidados).
     */
    public ReservationListItemDTO toListItemDTO(Reservation reservation) {
        if (reservation == null) return null;

        ReservationListItemDTO dto = new ReservationListItemDTO();
        dto.setId(reservation.getId());
        dto.setClientId(reservation.getClientId());
        dto.setBarberId(reservation.getBarberId());
        dto.setServiceId(reservation.getServiceId());
        dto.setStart(reservation.getStartTime());
        dto.setEnd(reservation.getEndTime());
        dto.setPrice(reservation.getPrice());
        dto.setStatus(mapStatus(reservation.getStatus()));

        return dto;
    }

    /**
     * Convierte una lista de Reservations a lista de ReservationListItemDTO.
     */
    public List<ReservationListItemDTO> toListItemDTOs(List<Reservation> reservations) {
        if (reservations == null) return List.of();
        
        return reservations.stream()
                .map(this::toListItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una lista de Reservations a lista de ReservationResponseDTO.
     */
    public List<ReservationResponseDTO> toResponseDTOs(List<Reservation> reservations) {
        if (reservations == null) return List.of();
        
        return reservations.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapea el estado de la reserva a formato legible para el frontend.
     */
    private String mapStatus(com.sw3.reservation_microservice.domain.model.ReservationStatus status) {
        if (status == null) return null;
        
        return switch (status) {
            case EN_ESPERA -> "En espera";
            case INASISTENCIA -> "Inasistencia";
            case EN_PROCESO -> "En proceso";
            case FINALIZADA -> "Finalizada";
            case CANCELADA -> "Cancelada";
        };
    }
}
