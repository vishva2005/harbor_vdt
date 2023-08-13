package com.harbor.calendly.model;

import com.harbor.calendly.entities.Availability;
import com.harbor.calendly.errors.AvailabilityException;
import com.harbor.calendly.errors.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Getter
@Setter
@Builder
public class AvailabilityDto {

    private DayOfWeek weekDay;

    // time in sec from 0000 hrs
    private Integer startTimeInSec;

    // time in sec from 0000 hrs
    private Integer durationInSec;

    private boolean isAvailable = true;

    // in cases where user wants to be specific they will send datetime in epoch seconds
    private Long startDateTimeInEpoch;

    // in cases where user wants to be specific they will send datetime in epoch seconds
    private Long endDateTimeInEpoch;

    public void validate() {
        if (weekDay != null) {
            checkCondition(startTimeInSec == null, "start time can not be null");
            checkCondition(durationInSec == null, "end time can not be null");
            checkCondition(durationInSec <= 0, "end time can not be less than start time");

            checkCondition(startDateTimeInEpoch != null, "startDateTimeInEpoch should be null");
            checkCondition(endDateTimeInEpoch != null, "endDateTimeInEpoch should be null");
        } else {
            checkCondition(startTimeInSec != null, "start time should be null");
            checkCondition(durationInSec != null, "end time should be null");

            checkCondition(startDateTimeInEpoch == null, "startDateTimeInEpoch can not be null");
            checkCondition(endDateTimeInEpoch == null, "endDateTimeInEpoch can not be null");
            checkCondition(endDateTimeInEpoch <= startDateTimeInEpoch, "endDateTimeInEpoch can not be less than startDateTimeInEpoch");
        }
    }

    private void checkCondition(boolean condition, String message) {
        if (condition) {
            throw new AvailabilityException(ErrorCode.INVALID_AVAILABILITY, message);
        }
    }

    public static Availability transformToAvailability(AvailabilityDto availabilityDto, Integer scheduleId) {
        Availability availability = new Availability();
        availability.setAvailable(availabilityDto.isAvailable());
        availability.setDurationInSec(availabilityDto.getDurationInSec());
        availability.setStartTimeInSec(availabilityDto.getStartTimeInSec());
        availability.setStartDateTimeInEpoch(availabilityDto.getStartDateTimeInEpoch());
        availability.setEndDateTimeInEpoch(availabilityDto.getEndDateTimeInEpoch());
        availability.setWeekDay(availabilityDto.getWeekDay());
        availability.setScheduleId(scheduleId);
        return availability;
    }

    public static AvailabilityDto transformToAvailabilityDto(Availability availability) {
        return AvailabilityDto.builder()
                .isAvailable(availability.isAvailable())
                .startTimeInSec(availability.getStartTimeInSec())
                .durationInSec(availability.getDurationInSec())
                .startDateTimeInEpoch(availability.getStartDateTimeInEpoch())
                .endDateTimeInEpoch(availability.getEndDateTimeInEpoch())
                .weekDay(availability.getWeekDay())
                .build();
    }

}
