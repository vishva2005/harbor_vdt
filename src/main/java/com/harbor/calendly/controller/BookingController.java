package com.harbor.calendly.controller;

import com.harbor.calendly.entities.BookingSlot;
import com.harbor.calendly.entities.Schedule;
import com.harbor.calendly.model.AvailableSlotDto;
import com.harbor.calendly.model.BookingSlotDto;
import com.harbor.calendly.service.BookingSlotService;
import com.harbor.calendly.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/schedules/{scheduleId}/")
public class BookingController {

    private final BookingSlotService bookingSlotService;
    private final ScheduleService scheduleService;

    @Autowired
    public BookingController(BookingSlotService bookingSlotService,
                             ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
        this.bookingSlotService = bookingSlotService;
    }

    @PostMapping("/slots")
    public void bookSlot(@PathVariable("scheduleId")int scheduleId,
                         @RequestBody BookingSlotDto bookingSlotDto) {
        bookingSlotDto.validate();
        Schedule schedule = scheduleService.getSchedule(scheduleId);
        BookingSlot bookingSlot = BookingSlotDto.transformToBookingSlot(bookingSlotDto, schedule.getUserId());
        bookingSlotService.bookSlotIfAvailable(bookingSlot, scheduleId);
    }

    @GetMapping("/slots")
    public List<AvailableSlotDto> getAvailableSlots(@PathVariable("scheduleId")int scheduleId,
                                                    @RequestParam("startDateTime") long startDateTime,
                                                    @RequestParam("endDateTime") long endDateTime) {
        Schedule schedule = scheduleService.getSchedule(scheduleId);
        return bookingSlotService.getAllAvailableSlots(scheduleId, schedule.getUserId(), startDateTime, endDateTime);
    }

}
