package com.harbor.calendly.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ScheduleAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer scheduleId;

    private String weekDay;

    /*
     * start time is stored in military time format i.e. 2:30pm would be stored as 1430
     */
    private int startTime;

    /*
     * end time is stored in military time format i.e. 2:30pm would be stored as 1430
     */
    private int endTime;

    private boolean isAvailable = true;

    private Date date;

}
