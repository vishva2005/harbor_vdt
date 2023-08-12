package com.harbor.calendly.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSlotDto {

    private String guestEmail;
    private String guestName;
    private String description;
    // date time in epoch
    private long startTimeInEpoch;
    private long endTimeInEpoch;
}
