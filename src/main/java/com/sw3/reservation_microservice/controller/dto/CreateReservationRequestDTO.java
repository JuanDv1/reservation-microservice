package com.sw3.reservation_microservice.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

//Objeto para peticiones POST
@Data
public class CreateReservationRequestDTO {
    private String clientId;
    private String barberId;
    private Long serviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;
    // El status se asigna por defecto en el backend
}