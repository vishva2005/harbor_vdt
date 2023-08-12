package com.harbor.calendly.controller;

import com.harbor.calendly.entities.Schedule;
import com.harbor.calendly.model.ScheduleDto;
import com.harbor.calendly.service.ScheduleService;
import com.harbor.calendly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService,
                              UserService userService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleDto createSchedule(@PathVariable("userId")int userId,
                               @RequestBody ScheduleDto scheduleDto) {
        scheduleDto.validate();
        userService.getUser(userId);
        Schedule schedule = scheduleService.createSchedule(ScheduleDto.transformToSchedule(scheduleDto, userId));
        return ScheduleDto.transformToScheduleDto(schedule);
    }

    @PatchMapping("/{scheduleId}")
    public ScheduleDto updateSchedule(@PathVariable("userId")int userId,
                               @PathVariable("scheduleId") Integer scheduleId,
                               @RequestBody ScheduleDto scheduleDto) {
        scheduleDto.validate();
        scheduleDto.setId(scheduleId);
        userService.getUser(userId);
        Schedule schedule = scheduleService.updateSchedule(ScheduleDto.transformToSchedule(scheduleDto, userId));
        return ScheduleDto.transformToScheduleDto(schedule);
    }

    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@PathVariable("userId")int userId,
                               @PathVariable("scheduleId") Integer scheduleId) {
        userService.getUser(userId);
        scheduleService.deleteSchedule(scheduleId);
    }

    @GetMapping("/{scheduleId}")
    public ScheduleDto getSchedule(@PathVariable("userId")int userId,
                                   @PathVariable("scheduleId") int scheduleId) {
        userService.getUser(userId);
        Schedule schedule = scheduleService.getSchedule(scheduleId);
        return ScheduleDto.transformToScheduleDto(schedule);
    }
}
