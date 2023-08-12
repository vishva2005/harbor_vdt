package com.harbor.calendly.controller;

import com.harbor.calendly.model.ScheduleDto;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/schedules")
public class ScheduleController {

    @PostMapping
    public ScheduleDto createSchedule(@PathVariable("userId")int userId,
                               @RequestBody ScheduleDto scheduleDto) {
        return null;
    }

    @PatchMapping
    public ScheduleDto updateSchedule(@PathVariable("userId")int userId,
                               @RequestBody ScheduleDto scheduleDto) {
        return null;
    }

    @DeleteMapping
    public void deleteSchedule(@PathVariable("userId")int userId,
                               @RequestBody ScheduleDto scheduleDto) {
    }

    @GetMapping("/{scheduleId}")
    public ScheduleDto getSchedule(@PathVariable("userId")int userId,
                                   @PathVariable("scheduleId") int scheduleId) {
        return null;
    }
}
