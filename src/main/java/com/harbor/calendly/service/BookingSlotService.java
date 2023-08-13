package com.harbor.calendly.service;

import com.harbor.calendly.dao.BookingSlotRepository;
import com.harbor.calendly.entities.Availability;
import com.harbor.calendly.entities.BookingSlot;
import com.harbor.calendly.errors.BookingSlotException;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.model.AvailableSlotDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void bookSlotIfAvailable(BookingSlot bookingSlot, Integer scheduleId) {
        List<Availability> availabilityList = availabilityService.getAvailabilities(scheduleId);

        validateSlotInUnavailableHours(availabilityList, bookingSlot);
        validateSlotOutsideAvailableHours(availabilityList, bookingSlot);

        int overlappingSlots = bookingSlotRepository.getCountOfOverlappingBookingSlots(bookingSlot.getUserId(), bookingSlot.getStartDateTimeInEpoch(),
                bookingSlot.getEndDateTimeInEpoch());
        if (overlappingSlots > 0) {
            throw new BookingSlotException(ErrorCode.SLOT_ALREADY_BOOKED, "slot already booked");
        }
        bookingSlotRepository.save(bookingSlot);
    }

    public List<AvailableSlotDto> getAllAvailableSlots(Integer scheduleId, Integer userId, Long startDateTime, Long endDateTime) {
        List<Availability> availabilityList = availabilityService.getAvailabilities(scheduleId);
        List<AvailableSlotDto> unavailableSlots = bookingSlotRepository.getBookedSlotInWindow(userId, startDateTime, endDateTime).stream()
                .map(bookingSlot -> new AvailableSlotDto(bookingSlot.getStartDateTimeInEpoch(), bookingSlot.getEndDateTimeInEpoch()))
                .collect(Collectors.toCollection(ArrayList::new));

        List<AvailableSlotDto> availableSlots = new ArrayList<>();
        for(Availability availability : availabilityList) {
            if (!availability.isAvailable()) {
                unavailableSlots.add(new AvailableSlotDto(availability.getStartDateTimeInEpoch(), availability.getEndDateTimeInEpoch()));
                continue;
            }
            if (availability.getWeekDay() != null) {
                availableSlots.addAll(getAvailableSlotsForWeekDay(availability, startDateTime, endDateTime));
            } else {
                getAvailableSlotsForFixed(availability, startDateTime, endDateTime).ifPresent(availableSlots::add);
            }
        }
        Collections.sort(unavailableSlots);
        Collections.sort(availableSlots);

        return getAvailableSlotsAfterRemovingUnavailable(availableSlots, unavailableSlots);
    }

    private List<AvailableSlotDto> getAvailableSlotsAfterRemovingUnavailable(List<AvailableSlotDto> availableSlots, List<AvailableSlotDto> unavailableSlots) {
        if (unavailableSlots.size() == 0 || availableSlots.size() == 0) {
            return availableSlots;
        }
        int idx1 = 0;
        int idx2 = 0;
        List<AvailableSlotDto> result = new ArrayList<>();
        long timeScanned = Math.min(unavailableSlots.get(0).getStartDateTime(), availableSlots.get(0).getStartDateTime());
        while(idx1 < availableSlots.size()) {
            if (idx2 == unavailableSlots.size()) {
                if (result.size() > 0 && result.get(result.size() - 1).getEndDateTime().equals(timeScanned)) {
                    result.get(result.size() - 1).setEndDateTime(availableSlots.get(idx1).getEndDateTime());
                } else {
                    result.add(new AvailableSlotDto(timeScanned, availableSlots.get(idx1).getEndDateTime()));
                }
                idx1++;
                if (idx1 < availableSlots.size()) {
                    timeScanned = availableSlots.get(idx1 + 1).getStartDateTime();
                }
                continue;
            }
            if (unavailableSlots.get(idx2).containsTimeInclusive(timeScanned)) {
                if (availableSlots.get(idx1).getStartDateTime() >= timeScanned) {
                    timeScanned = Math.min(availableSlots.get(idx1).getStartDateTime(), unavailableSlots.get(idx2).getEndDateTime());
                } else {
                    timeScanned = Math.min(availableSlots.get(idx1).getEndDateTime(), unavailableSlots.get(idx2).getEndDateTime());
                }
                if (unavailableSlots.get(idx2).getEndDateTime().equals(timeScanned)) {
                    idx2++;
                }
            } else if (availableSlots.get(idx1).containsTimeInclusive(timeScanned)) {
                long closingTime = Math.min(availableSlots.get(idx1).getEndDateTime(), unavailableSlots.get(idx2).getStartDateTime());
                if (result.size() > 0 && result.get(result.size() - 1).getEndDateTime().equals(timeScanned)) {
                    result.get(result.size() - 1).setEndDateTime(closingTime);
                } else {
                    result.add(new AvailableSlotDto(timeScanned, closingTime));
                }
                timeScanned = closingTime;
                if (availableSlots.get(idx1).getEndDateTime().equals(closingTime)) {
                    idx1++;
                }
            } else {
                timeScanned = Math.min(unavailableSlots.get(idx2).getStartDateTime(), availableSlots.get(idx1).getStartDateTime());
            }
        }
        return result;
    }

    private Optional<AvailableSlotDto> getAvailableSlotsForFixed(Availability availability, Long startEpoch, Long endEpoch) {
        if (availability.getStartDateTimeInEpoch() >= endEpoch || availability.getEndDateTimeInEpoch() <= startEpoch) {
            return Optional.empty();
        }
        return Optional.of(new AvailableSlotDto(Math.max(availability.getStartDateTimeInEpoch(), startEpoch),
                Math.min(availability.getEndDateTimeInEpoch(), endEpoch)));
    }

    private List<AvailableSlotDto> getAvailableSlotsForWeekDay(Availability availability, Long startEpoch, Long endEpoch) {
        List<AvailableSlotDto> availableSlotDtoList = new ArrayList<>();
        LocalDateTime startDateTime = Instant.ofEpochSecond(startEpoch).atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime endDateTime = Instant.ofEpochSecond(endEpoch).atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime weekStartDateTime = startDateTime.truncatedTo(ChronoUnit.DAYS).with(TemporalAdjusters.previousOrSame(availability.getWeekDay()))
                .plusSeconds(availability.getStartTimeInSec());
        LocalDateTime weekEndDateTime = weekStartDateTime.plusSeconds(availability.getDurationInSec());
        while(weekStartDateTime.isBefore(endDateTime)) {
            if (!weekEndDateTime.isBefore(startDateTime)) {
                AvailableSlotDto availableSlotDto = new AvailableSlotDto(getMaxDateTime(weekStartDateTime, startDateTime),
                        getMinDateTime(weekEndDateTime, endDateTime));
                availableSlotDtoList.add(availableSlotDto);
            }
            weekStartDateTime = weekStartDateTime.with(TemporalAdjusters.next(availability.getWeekDay()));
            weekEndDateTime = weekEndDateTime.with(TemporalAdjusters.next(availability.getWeekDay()));
        }
        return availableSlotDtoList;
    }

    private long getMaxDateTime(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1.isBefore(dateTime2)) {
            return dateTime2.atZone(ZoneId.systemDefault()).toEpochSecond();
        }
        return dateTime1.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    private long getMinDateTime(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1.isAfter(dateTime2)) {
            return dateTime2.atZone(ZoneId.systemDefault()).toEpochSecond();
        }
        return dateTime1.atZone(ZoneId.systemDefault()).toEpochSecond();
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
