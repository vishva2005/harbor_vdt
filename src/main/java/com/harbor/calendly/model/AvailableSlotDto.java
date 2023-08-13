package com.harbor.calendly.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneId;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AvailableSlotDto implements Comparable<AvailableSlotDto> {

    private Long startDateTime;
    private Long endDateTime;

    @Override
    public int compareTo(AvailableSlotDto availableSlotDto) {
        return Long.compare(this.startDateTime, availableSlotDto.getStartDateTime());
    }
    public boolean containsTimeInclusive(long time) {
        if (time < startDateTime) {
            return false;
        }
        if (time > endDateTime) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append("startDate : ").append(Instant.ofEpochSecond(startDateTime).atZone(ZoneId.systemDefault()).toLocalDateTime());
        sb.append(" endDate : ").append(Instant.ofEpochSecond(endDateTime).atZone(ZoneId.systemDefault()).toLocalDateTime());
        sb.append(")");
        return sb.toString();
    }

}
