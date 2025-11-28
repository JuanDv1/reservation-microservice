package com.sw3.reservation_microservice.access;

import com.sw3.reservation_microservice.domain.model.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarberRepository extends JpaRepository<Barber, String> {

    /**
     * Busca un barbero activo por su ID.
     */
    Optional<Barber> findByBarberIdAndActiveTrue(String barberId);

    /**
     * Verifica si un barbero existe y está activo.
     */
    boolean existsByBarberIdAndActiveTrue(String barberId);
}
