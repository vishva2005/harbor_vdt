package com.harbor.calendly.controller;

import com.harbor.calendly.model.AvailabilityDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/schedules/{scheduleId}")
public class AvailabilityController {

    @PutMapping("/availability")
    public void setAvailability(@PathVariable("userId")int userId,
                                @PathVariable("scheduleId") int scheduleId,
                                @RequestBody List<AvailabilityDto> availabilityDto) {
    }

    @GetMapping("/availability")
    public List<AvailabilityDto> getAvailability(@PathVariable("userId") int userId,
                                                 @PathVariable("scheduleId")int scheduleId) {
        return new ArrayList<>();
    }

}
