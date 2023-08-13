package com.harbor.calendly.service;

import com.harbor.calendly.dao.BookingSlotRepository;
import com.harbor.calendly.entities.Availability;
import com.harbor.calendly.entities.BookingSlot;
import com.harbor.calendly.errors.BookingSlotException;
import com.harbor.calendly.errors.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class BookingSlotService {

    private static final Object MUTEX = new Object();
    private final BookingSlotRepository bookingSlotRepository;
    private final AvailabilityService availabilityService;

    @Autowired
    public BookingSlotService(BookingSlotRepository bookingSlotRepository,
                              AvailabilityService availabilityService) {
        this.bookingSlotRepository = bookingSlotRepository;
        this.availabilityService = availabilityService;
    }

    public void bookSlotIfAvailable(BookingSlot bookingSlot, Integer scheduleId) {
        List<Availability> availabilityList = availabilityService.getAvailabilities(scheduleId);

        validateSlotInUnavailableHours(availabilityList, bookingSlot);
        validateSlotOutsideAvailableHours(availabilityList, bookingSlot);

        synchronized (MUTEX) {
            int overlappingSlots = bookingSlotRepository.getCountOfOverlappingBookingSlots(bookingSlot.getUserId(), bookingSlot.getStartDateTimeInEpoch(),
                    bookingSlot.getEndDateTimeInEpoch());
            if (overlappingSlots > 0) {
                throw new BookingSlotException(ErrorCode.SLOT_ALREADY_BOOKED, "slot already booked");
            }
            bookingSlotRepository.save(bookingSlot);
        }
    }

    private void validateSlotInUnavailableHours(List<Availability> availabilityList, BookingSlot bookingSlot) {
        availabilityList.stream()
                .filter(availability -> availability.getWeekDay() == null)
                .filter(availability -> !availability.isAvailable())
                .filter(availability -> bookingOverlapsSlot(bookingSlot, availability.getStartDateTimeInEpoch(), availability.getEndDateTimeInEpoch()))
                .findFirst()
                .ifPresent(e -> {
                    throw new BookingSlotException(ErrorCode.SLOT_OUTSIDE_AVAILABLE_HOURS, "slot outside available hours");
                });
    }

    private void validateSlotOutsideAvailableHours(List<Availability> availabilityList, BookingSlot bookingSlot) {
        boolean inAvailableSlot = availabilityList.stream()
                .filter(availability -> availability.getWeekDay() == null)
                .filter(availability -> availability.isAvailable())
                .filter(availability -> availability.getStartDateTimeInEpoch() <= bookingSlot.getStartDateTimeInEpoch())
                .filter(availability -> availability.getEndDateTimeInEpoch() >= bookingSlot.getEndDateTimeInEpoch())
                .findFirst().isPresent();

        if (inAvailableSlot) {
            return;
        }
        availabilityList.stream()
                .filter(availability -> availability.getWeekDay() != null)
                .filter(availability -> availability.isAvailable())
                .filter(availability -> bookingFallsInAvailability(availability.getWeekDay(), availability.getStartTimeInSec(),
                        availability.getDurationInSec(), bookingSlot.getStartDateTimeInEpoch(), bookingSlot.getEndDateTimeInEpoch()))
                .findFirst()
                .orElseThrow(() -> new BookingSlotException(ErrorCode.SLOT_OUTSIDE_AVAILABLE_HOURS, "slot outside available hours"));
    }

    private boolean bookingOverlapsSlot(BookingSlot bookingSlot, long startDateTimeEpoch, long endDateTimeEpoch) {
        if (endDateTimeEpoch <= bookingSlot.getStartDateTimeInEpoch()) {
            return false;
        }
        if (startDateTimeEpoch >= bookingSlot.getEndDateTimeInEpoch()) {
            return false;
        }
        return true;
    }

    private boolean bookingFallsInAvailability(DayOfWeek weekDay,
                                             int startTimeInSec,
                                             int durationInSec,
                                             long bookingStartEpoch,
                                             long bookingEndEpoch) {
        LocalDateTime bookingDateTime = Instant.ofEpochSecond(bookingStartEpoch).atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime weekStartDateTime = bookingDateTime.truncatedTo(ChronoUnit.DAYS).with(TemporalAdjusters.previousOrSame(weekDay))
                .plusSeconds(startTimeInSec);
        LocalDateTime weekEndDateTime = weekStartDateTime.plusSeconds(durationInSec);
        if (weekEndDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() >= bookingEndEpoch) {
            return true;
        }
        weekStartDateTime = weekStartDateTime.with(TemporalAdjusters.next(weekDay));
        weekEndDateTime = weekStartDateTime.plusSeconds(durationInSec);
        if (weekEndDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() >= bookingEndEpoch &&
                weekStartDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() <= bookingStartEpoch) {
            return true;
        }
        return false;
    }

}
