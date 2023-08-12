package com.harbor.calendly.controller;

import com.harbor.calendly.model.AvailabilityDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/schedules/{scheduleId}")
public class AvailabilityController {

    @PostMapping("/setAvailability")
    public void setAvailability(@PathVariable("userId")int userId,
                                @PathVariable("scheduleId") int scheduleId,
                                @RequestBody AvailabilityDto availabilityDto) {

    }

    @GetMapping("/getAvailability")
    public List<AvailabilityDto> getAvailability(@PathVariable("userId") int userId,
                                                 @PathVariable("scheduleId")int scheduleId) {
        return new ArrayList<>();
    }
}
