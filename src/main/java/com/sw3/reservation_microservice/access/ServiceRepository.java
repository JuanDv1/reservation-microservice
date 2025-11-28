package com.sw3.reservation_microservice.access;

import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    /**
     * Busca un servicio activo por su ID.
     */
    Optional<ServiceEntity> findByServiceIdAndActiveTrue(Long serviceId);

    /**
     * Verifica si un servicio existe y está activo.
     */
    boolean existsByServiceIdAndActiveTrue(Long serviceId);
}
