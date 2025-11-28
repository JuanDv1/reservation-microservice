package com.sw3.reservation_microservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sw3.reservation_microservice.domain.state.CanceladaState;
import com.sw3.reservation_microservice.domain.state.EnEsperaState;
import com.sw3.reservation_microservice.domain.state.EnProcesoState;
import com.sw3.reservation_microservice.domain.state.FinalizadaState;
import com.sw3.reservation_microservice.domain.state.InasistenciaState;
import com.sw3.reservation_microservice.domain.state.ReservationState;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String barberId;

    @Column(nullable = false)
    private Long serviceId;
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Transient
    @JsonIgnore
    private ReservationState state;

    public Reservation() {
        this.state = new EnEsperaState();
        this.status = ReservationStatus.EN_ESPERA;
    }

    // Este método se ejecuta automáticamente cuando Hibernate lee el objeto de la BD
    @PostLoad
    public void loadState() {
        switch (this.status) {
            case EN_ESPERA:
                this.state = new EnEsperaState();
                break;
            case EN_PROCESO:
                this.state = new EnProcesoState();
                break;
            case FINALIZADA:
                this.state = new FinalizadaState();
                break;
            case CANCELADA:
                this.state = new CanceladaState();
                break;
            case INASISTENCIA:
                this.state = new InasistenciaState();
                break;
            default:
                this.state = new EnEsperaState();
        }
    }

    public void cancelar() {
        state.cancelar(this);
    }

    public void iniciarServicio() {
        state.iniciarServicio(this);
    }

    public void finalizarServicio() {
        state.finalizarServicio(this);
    }
}
