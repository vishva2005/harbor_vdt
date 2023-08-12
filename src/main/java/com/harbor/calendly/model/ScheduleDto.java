package com.harbor.calendly.model;

import com.harbor.calendly.entities.Schedule;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.errors.ScheduleException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.TimeZone;

@Getter
@Setter
@Builder
public class ScheduleDto {

    private Integer id;

    private String timezone;

    private String name;

    private String description;

    public void validate() {
        if (!Set.of(TimeZone.getAvailableIDs()).contains(timezone)) {
            throw new ScheduleException(ErrorCode.INVALID_TIMEZONE, "timezone "+timezone+" is not valid");
        }
    }

    public static final Schedule transformToSchedule(ScheduleDto scheduleDto, Integer userId) {
        Schedule schedule = new Schedule();
        schedule.setUserId(userId);
        schedule.setDescription(scheduleDto.getDescription());
        schedule.setTimezone(scheduleDto.getTimezone());
        schedule.setName(scheduleDto.getName());
        schedule.setId(scheduleDto.getId());
        return schedule;
    }

    public static final ScheduleDto transformToScheduleDto(Schedule schedule) {
        return ScheduleDto.builder()
                .id(schedule.getId())
                .description(schedule.getDescription())
                .name(schedule.getName())
                .timezone(schedule.getTimezone())
                .build();
    }

}
