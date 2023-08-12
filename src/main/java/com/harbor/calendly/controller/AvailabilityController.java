package com.harbor.calendly.controller;

import com.harbor.calendly.entities.Availability;
import com.harbor.calendly.model.AvailabilityDto;
import com.harbor.calendly.service.AvailabilityService;
import com.harbor.calendly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/{userId}/schedules/{scheduleId}")
public class AvailabilityController {

    private UserService userService;
    private AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(UserService userService, AvailabilityService availabilityService) {
        this.userService = userService;
        this.availabilityService = availabilityService;
    }

    @PutMapping("/availability")
    public void setAvailability(@PathVariable("userId")int userId,
                                @PathVariable("scheduleId") int scheduleId,
                                @RequestBody List<AvailabilityDto> availabilityDtoList) {
        List<Availability> availabilityList = availabilityDtoList.stream()
                        .peek(availabilityDto -> availabilityDto.validate())
                        .map(availabilityDto -> AvailabilityDto.transformToAvailability(availabilityDto, scheduleId))
                        .collect(Collectors.toList());
        userService.getUser(userId);
        availabilityService.addAvailabilities(scheduleId, availabilityList);
    }

    @GetMapping("/availability")
    public List<AvailabilityDto> getAvailability(@PathVariable("userId") int userId,
                                                 @PathVariable("scheduleId")int scheduleId) {
        userService.getUser(userId);
        return availabilityService.getAvailabilities(scheduleId)
                .stream()
                .map(AvailabilityDto::transformToAvailabilityDto)
                .collect(Collectors.toList());
    }

}
