package com.harbor.calendly.service;

import com.harbor.calendly.dao.AvailabilityRepository;
import com.harbor.calendly.entities.Availability;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.errors.ScheduleException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvailabilityService {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityService.class);
    private final AvailabilityRepository availabilityRepository;

    @Autowired
    public AvailabilityService(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public void addAvailabilities(Integer scheduleId, List<Availability> availabilityList) {
        try {
            availabilityRepository.deleteAvailabilityByScheduleId(scheduleId);
            availabilityRepository.saveAll(availabilityList);
        } catch (DataIntegrityViolationException ex) {
            logger.atError()
                    .setCause(ex)
                    .setMessage("failed while persisting availability")
                    .log();
            if (ex.getMessage().toLowerCase().contains("referential integrity")) {
                throw new ScheduleException(ErrorCode.SCHEDULE_NOT_EXISTS, ex, "schedule not exists");
            } else {
                throw new ScheduleException(ErrorCode.UNKNOWN_ERROR, ex, "unidentified db constraint exception");
            }
        }

    }

    public List<Availability> getAvailabilities(Integer scheduleId) {
        return availabilityRepository.getAvailabilityByScheduleId(scheduleId);
    }
}
