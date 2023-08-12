package com.harbor.calendly.dao;

import com.harbor.calendly.entities.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    List<Availability> getAvailabilityByScheduleId(Integer scheduleId);

    void deleteAvailabilityByScheduleId(Integer scheduleId);


}
