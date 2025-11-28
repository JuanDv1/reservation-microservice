package com.sw3.reservation_microservice.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    private Long id;
    private String clientId;
    private String barberId;
    private Long serviceId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Double price;
    private String status;
}
