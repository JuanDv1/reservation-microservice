package com.sw3.reservation_microservice.access;

import com.sw3.reservation_microservice.domain.model.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {

    List<WorkShift> findByBarberId(String barberId);

    List<WorkShift> findByBarberIdAndDayOfWeek(String barberId, String dayOfWeek);

    @Query("SELECT ws FROM WorkShift ws WHERE ws.barberId = :barberId AND ws.dayOfWeek = :dayOfWeek")
    List<WorkShift> findShiftsForDay(@Param("barberId") String barberId, @Param("dayOfWeek") String dayOfWeek);
}
