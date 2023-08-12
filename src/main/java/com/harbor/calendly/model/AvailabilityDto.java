package com.harbor.calendly.model;

import com.harbor.calendly.errors.AvailabilityException;
import com.harbor.calendly.errors.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AvailabilityDto {

    public enum WEEKDAY { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }

    private WEEKDAY weekDay;

    // time in sec from 0000 hrs
    private Integer startTimeInSec;

    // time in sec from 0000 hrs
    private Integer endTimeInSec;

    private boolean isAvailable = true;

    // in cases where user wants to be specific they will send datetime in epoch seconds
    private Long startDateTimeInEpoch;

    // in cases where user wants to be specific they will send datetime in epoch seconds
    private Long endDateTimeInEpoch;

    public void validate() {
        if (weekDay != null) {
            checkCondition(startTimeInSec == null, "start time can not be null");
            checkCondition(endTimeInSec == null, "end time can not be null");
            checkCondition(endTimeInSec <= startTimeInSec, "end time can not be less than start time");

            checkCondition(startDateTimeInEpoch != null, "startDateTimeInEpoch should be null");
            checkCondition(endDateTimeInEpoch != null, "endDateTimeInEpoch should be null");
        } else {
            checkCondition(startTimeInSec != null, "start time should be null");
            checkCondition(endTimeInSec != null, "end time should be null");

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

}
