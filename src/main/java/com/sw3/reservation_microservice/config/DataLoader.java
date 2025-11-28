package com.sw3.reservation_microservice.config;

import com.sw3.reservation_microservice.access.BarberRepository;
import com.sw3.reservation_microservice.access.ServiceRepository;
import com.sw3.reservation_microservice.domain.model.Barber;
import com.sw3.reservation_microservice.domain.model.BarberSchedule;
import com.sw3.reservation_microservice.domain.model.BarberShift;
import com.sw3.reservation_microservice.domain.model.ServiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Carga inicial de datos de barberos y servicios para testing.
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {
        loadServices();
        loadBarbers();
    }

    private void loadBarbers() {
        if (barberRepository.count() == 0) {
            // Barbero 1: Carlos Rojas (Horario estándar completo)
            Barber carlos = createBarber(
                "barber001",
                "Carlos",
                "Rojas",
                "carlos.rojas@barberia.com",
                "3001234567",
                "https://randomuser.me/api/portraits/men/32.jpg",
                "Maestro de la tijera clásica.",
                "Disponible",
                "Activo",
                "101,102,107,201,202"
            );
            addStandardSchedule(carlos);
            barberRepository.save(carlos);

            // Barbero 2: Miguel Rodríguez (Horario estándar, No Disponible)
            Barber miguel = createBarber(
                "barber002",
                "Miguel",
                "Rodríguez",
                "miguel.rodriguez@barberia.com",
                "3009876543",
                "https://randomuser.me/api/portraits/men/45.jpg",
                "Especialista en cortes modernos.",
                "No Disponible",
                "Activo",
                "101,103,201"
            );
            addStandardSchedule(miguel);
            barberRepository.save(miguel);

            // Barbero 3: Juan López (Solo trabaja Lunes a Viernes)
            Barber juan = createBarber(
                "barber003",
                "Juan",
                "López",
                "juan.lopez@barberia.com",
                "3005551234",
                "https://randomuser.me/api/portraits/men/28.jpg",
                "Experto en barbas y afeitados.",
                "Disponible",
                "Activo",
                "102,103,201,202"
            );
            addWeekdaySchedule(juan); // Lunes a Viernes
            barberRepository.save(juan);

            // Barbero 4: Diego Martínez (Inactivo - soft delete)
            Barber diego = createBarber(
                "barber004",
                "Diego",
                "Martínez",
                "diego.martinez@barberia.com",
                "3007778888",
                "https://randomuser.me/api/portraits/men/55.jpg",
                "Barbero con 20 años de experiencia.",
                "No Disponible",
                "Inactivo",
                "101,102,103"
            );
            addStandardSchedule(diego);
            barberRepository.save(diego);

            // Barbero 5: Andrés Silva (Solo tardes)
            Barber andres = createBarber(
                "barber005",
                "Andrés",
                "Silva",
                "andres.silva@barberia.com",
                "3009998888",
                "https://randomuser.me/api/portraits/men/40.jpg",
                "Especialista en cortes de tendencia.",
                "Disponible",
                "Activo",
                "101,107,201"
            );
            addAfternoonSchedule(andres); // Solo tardes de Lunes a Sábado
            barberRepository.save(andres);

            System.out.println("✅ Barberos cargados: " + barberRepository.count());
        }
    }

    private Barber createBarber(String id, String name, String lastName, String email, 
                                String phone, String photoUrl, String bio,
                                String availabilityStatus, String systemStatus, String serviceIds) {
        Barber barber = new Barber();
        barber.setBarberId(id);
        barber.setName(name);
        barber.setLastName(lastName);
        barber.setEmail(email);
        barber.setPhone(phone);
        barber.setPhotoUrl(photoUrl);
        barber.setBio(bio);
        barber.setAvailabilityStatus(availabilityStatus);
        barber.setSystemStatus(systemStatus);
        barber.setServiceIds(serviceIds);
        barber.setCreatedAt(LocalDateTime.now());
        barber.setUpdatedAt(LocalDateTime.now());
        return barber;
    }

    /**
     * Horario estándar: Lun-Vie (8-12, 14-18), Vie (8-12, 14-19), Sáb (8-14)
     */
    private void addStandardSchedule(Barber barber) {
        // Lunes (1)
        addDayWithShifts(barber, 1, "08:00", "12:00", "14:00", "18:00");
        // Martes (2)
        addDayWithShifts(barber, 2, "08:00", "12:00", "14:00", "18:00");
        // Miércoles (3)
        addDayWithShifts(barber, 3, "08:00", "12:00", "14:00", "18:00");
        // Jueves (4)
        addDayWithShifts(barber, 4, "08:00", "12:00", "14:00", "18:00");
        // Viernes (5)
        addDayWithShifts(barber, 5, "08:00", "12:00", "14:00", "19:00");
        // Sábado (6)
        addDayWithOneShift(barber, 6, "08:00", "14:00");
        // Domingo (0) - Sin turnos
        addDayWithoutShifts(barber, 0);
    }

    /**
     * Solo Lunes a Viernes
     */
    private void addWeekdaySchedule(Barber barber) {
        for (int day = 1; day <= 5; day++) {
            addDayWithShifts(barber, day, "08:00", "12:00", "14:00", "18:00");
        }
        addDayWithoutShifts(barber, 6); // Sábado sin turnos
        addDayWithoutShifts(barber, 0); // Domingo sin turnos
    }

    /**
     * Solo tardes (Lunes a Sábado)
     */
    private void addAfternoonSchedule(Barber barber) {
        for (int day = 1; day <= 6; day++) {
            addDayWithOneShift(barber, day, "14:00", "20:00");
        }
        addDayWithoutShifts(barber, 0); // Domingo sin turnos
    }

    private void addDayWithShifts(Barber barber, int dayOfWeek, 
                                  String shift1Start, String shift1End,
                                  String shift2Start, String shift2End) {
        BarberSchedule schedule = new BarberSchedule(dayOfWeek);
        schedule.addShift(new BarberShift(LocalTime.parse(shift1Start), LocalTime.parse(shift1End)));
        schedule.addShift(new BarberShift(LocalTime.parse(shift2Start), LocalTime.parse(shift2End)));
        barber.addScheduleDay(schedule);
    }

    private void addDayWithOneShift(Barber barber, int dayOfWeek, String start, String end) {
        BarberSchedule schedule = new BarberSchedule(dayOfWeek);
        schedule.addShift(new BarberShift(LocalTime.parse(start), LocalTime.parse(end)));
        barber.addScheduleDay(schedule);
    }

    private void addDayWithoutShifts(Barber barber, int dayOfWeek) {
        BarberSchedule schedule = new BarberSchedule(dayOfWeek);
        barber.addScheduleDay(schedule);
    }

    private void loadServices() {
        if (serviceRepository.count() == 0) {
            serviceRepository.save(new ServiceEntity(
                1L,
                "Corte Clásico",
                "Corte de cabello tradicional con tijera y máquina",
                new BigDecimal("25000"),
                30,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
            ));

            serviceRepository.save(new ServiceEntity(
                2L,
                "Corte + Barba",
                "Corte de cabello y arreglo de barba completo",
                new BigDecimal("40000"),
                45,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
            ));

            serviceRepository.save(new ServiceEntity(
                3L,
                "Afeitado Clásico",
                "Afeitado tradicional con navaja y toallas calientes",
                new BigDecimal("20000"),
                25,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
            ));

            serviceRepository.save(new ServiceEntity(
                4L,
                "Corte Premium",
                "Corte personalizado con lavado y masaje capilar",
                new BigDecimal("50000"),
                60,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
            ));

            serviceRepository.save(new ServiceEntity(
                5L,
                "Tratamiento Capilar",
                "Aplicación de tratamiento nutritivo para el cabello",
                new BigDecimal("35000"),
                40,
                false,  // Servicio inactivo para testing
                LocalDateTime.now(),
                LocalDateTime.now()
            ));

            System.out.println("✅ Servicios cargados: " + serviceRepository.count());
        }
    }
}
