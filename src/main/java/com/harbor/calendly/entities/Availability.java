package com.harbor.calendly.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "availability_tbl")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "schedule_id")
    private Integer scheduleId;

    @Column(name = "weekday")
    private String weekDay;

    @Column(name = "start_time_in_sec")
    // time in sec from 0000 hrs
    private Integer startTimeInSec;

    @Column(name = "end_time_in_sec")
    // time in sec from 0000 hrs
    private Integer endTimeInSec;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @Column(name = "start_date_time_in_epoch")
    // in cases where user wants to be specific they will send datetime in epoch seconds
    private Long startDateTimeInEpoch;

    @Column(name = "end_date_time_in_epoch")
    // in cases where user wants to be specific they will send datetime in epoch seconds
    private Long endDateTimeInEpoch;

}
