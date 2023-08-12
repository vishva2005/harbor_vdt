package com.harbor.calendly.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ScheduleDto {

    private Integer id;

    private String timeZone;

    private String name;

    private String description;

}
