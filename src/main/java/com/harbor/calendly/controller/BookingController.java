package com.harbor.calendly.controller;

import com.harbor.calendly.model.AvailabilityDto;
import com.harbor.calendly.model.BookSlotDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schedules/{scheduleId}/")
public class BookingController {

    @PostMapping("/bookSlot")
    public void bookSlot(@PathVariable("scheduleId")int scheduleId,
                         @RequestBody BookSlotDto bookSlotDto) {
    }

    @GetMapping("/availableSlots")
    public List<AvailabilityDto> getAvailableSlots(@PathVariable("userId")int userId,
                                                   @PathVariable("scheduleId")int scheduleId,
                                                   @RequestParam("startTime") long startTime,
                                                   @RequestParam("endTime") long endTime) {
        return new ArrayList<>();
    }

}
