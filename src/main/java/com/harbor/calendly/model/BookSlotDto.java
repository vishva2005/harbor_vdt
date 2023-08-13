package com.harbor.calendly.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookSlotDto {

    private String guestEmail;
    private String guestName;
    private String description;
    // date time in epoch
    private long startDateTimeInEpoch;
    private long endDateTimeInEpoch;
}
