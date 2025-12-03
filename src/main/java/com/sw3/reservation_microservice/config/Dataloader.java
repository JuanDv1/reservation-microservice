package com.sw3.reservation_microservice.config;

import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Dataloader implements CommandLineRunner {

    private final ServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {
        // Al usar ddl-auto=create, cargamos las réplicas si la tabla está vacía.
        if (serviceRepository.count() == 0) {
            cargarReplicasServicios();
        }
    }

    private void cargarReplicasServicios() {
        List<ServiceEntity> servicios = new ArrayList<>();

        // NOTA: Los IDs coinciden exactamente con el orden de inserción del MS de Servicios.
        // Precios en Double y Duración en Integer.
        // AvailabilityStatus = true para permitir hacer reservas en la demo.

        // --- Categoría: Combos ---
        servicios.add(crearServicio(1L, 50000.0, 60)); // Padre e Hijo
        servicios.add(crearServicio(2L, 45000.0, 50)); // Corte + Barba
        servicios.add(crearServicio(3L, 70000.0, 90)); // Combo VIP

        // --- Categoría: Cortes de Cabello ---
        servicios.add(crearServicio(4L, 25000.0, 30)); // Corte Clásico
        servicios.add(crearServicio(5L, 30000.0, 40)); // Degradado (Fade)
        servicios.add(crearServicio(6L, 15000.0, 20)); // Rapado Total

        // --- Categoría: Cortes faciales ---
        servicios.add(crearServicio(7L, 15000.0, 20)); // Perfilado de Barba
        servicios.add(crearServicio(8L, 25000.0, 30)); // Afeitado de Lujo
        servicios.add(crearServicio(9L, 10000.0, 10)); // Diseño de Cejas

        // --- Categoría: Tratamiento capilar ---
        servicios.add(crearServicio(10L, 90000.0, 120)); // Keratina Hombre
        servicios.add(crearServicio(11L, 20000.0, 20));  // Mascarilla Negra
        servicios.add(crearServicio(12L, 25000.0, 30));  // Exfoliación

        // --- Categoría: Otros ---
        servicios.add(crearServicio(13L, 35000.0, 40));  // Tinte de Barba
        servicios.add(crearServicio(14L, 150000.0, 240)); // Platinado
        servicios.add(crearServicio(15L, 12000.0, 10));   // Lavado Especial

        serviceRepository.saveAll(servicios);
        System.out.println(">>> Réplicas de Servicios cargadas en Reservation-MS: " + servicios.size());
    }

    private ServiceEntity crearServicio(Long id, Double price, Integer duration) {
        ServiceEntity servicio = new ServiceEntity();
        servicio.setId(id);            // ID manual para consistencia
        servicio.setPrice(price);      // Precio para calcular total reserva
        servicio.setDuration(duration);// Duración para calcular hora fin
        servicio.setAvailabilityStatus(true); // TRUE para que el validador permita reservar
        return servicio;
    }
}