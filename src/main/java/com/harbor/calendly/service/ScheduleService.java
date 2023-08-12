package com.harbor.calendly.service;

import com.harbor.calendly.dao.ScheduleRepository;
import com.harbor.calendly.entities.Schedule;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.errors.ScheduleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Schedule createSchedule(Schedule schedule) {
        try {
            return scheduleRepository.save(schedule);
        } catch (DataIntegrityViolationException ex) {
            logger.atError()
                    .setCause(ex)
                    .setMessage("failed while persisting schedule")
                    .log();
            throw new ScheduleException(ErrorCode.SCHEDULE_ALREADY_EXISTS, ex, "schedule already exists");
        }
    }

    public Schedule updateSchedule(Schedule schedule) {
        return scheduleRepository.findById(schedule.getId())
                .map(ignored -> scheduleRepository.save(schedule))
                .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_EXISTS, "schedule does not exist"));
    }

    public void deleteSchedule(Integer scheduleId) {
        scheduleRepository.findById(scheduleId)
                .ifPresentOrElse(scheduleRepository::delete,
                        () -> {
                    throw new ScheduleException(ErrorCode.SCHEDULE_NOT_EXISTS, "schedule does not exist");
                });
    }

    public Schedule getSchedule(Integer scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ErrorCode.SCHEDULE_NOT_EXISTS, "schedule does not exist"));
    }
}
