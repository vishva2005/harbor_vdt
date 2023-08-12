package com.harbor.calendly.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDto {

    private String weekDay;

    // start time will be in the military format i.e 2:30pm will be 1430
    private int startTime;

    // end time will be in the military format i.e 2:30pm will be 1430
    private int endTime;

    private boolean isAvailable;

    // in cases where user wants to be specific they will send datetime in epoch seconds
    private long startDateTimeInEpoch;

    // in cases where user wants to be specific they will send datetime in epoch seconds
    private long endDateTimeInEpoch;

}
