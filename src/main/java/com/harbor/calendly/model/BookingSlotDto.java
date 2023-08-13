package com.harbor.calendly.model;

import com.harbor.calendly.entities.BookingSlot;
import com.harbor.calendly.errors.AvailabilityException;
import com.harbor.calendly.errors.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class BookingSlotDto {

    private String guestEmail;
    private String guestName;
    private String description;
    // date time in epoch
    private long startDateTimeInEpoch;
    private long endDateTimeInEpoch;

    public void validate() {
        checkCondition(guestEmail == null, "guest email can not be null");
        checkCondition(guestName == null, "guest name can not be null");
        checkCondition(description == null, "description can not be null");
        checkCondition(startDateTimeInEpoch < Instant.now().getEpochSecond(), "start time can not be in past");
        checkCondition(endDateTimeInEpoch < Instant.now().getEpochSecond(), "end time can not be in past");
        checkCondition(startDateTimeInEpoch >= endDateTimeInEpoch, "end time can not be less than start date time");
    }

    private void checkCondition(boolean condition, String message) {
        if (condition) {
            throw new AvailabilityException(ErrorCode.INVALID_SLOT, message);
        }
    }

    public static BookingSlot transformToBookingSlot(BookingSlotDto bookingSlotDto, Integer userId) {
        BookingSlot bookingSlot = new BookingSlot();
        bookingSlot.setDescription(bookingSlotDto.getDescription());
        bookingSlot.setGuestEmail(bookingSlotDto.getGuestEmail());
        bookingSlot.setGuestName(bookingSlotDto.getGuestName());
        bookingSlot.setStartDateTimeInEpoch(bookingSlotDto.getStartDateTimeInEpoch());
        bookingSlot.setEndDateTimeInEpoch(bookingSlotDto.getEndDateTimeInEpoch());
        bookingSlot.setUserId(userId);
        return bookingSlot;
    }

    public static BookingSlotDto transformToBookingSlotDto(BookingSlot bookingSlot) {
        return BookingSlotDto.builder()
                .guestName(bookingSlot.getGuestName())
                .guestEmail(bookingSlot.getGuestEmail())
                .description(bookingSlot.getDescription())
                .startDateTimeInEpoch(bookingSlot.getStartDateTimeInEpoch())
                .endDateTimeInEpoch(bookingSlot.getEndDateTimeInEpoch())
                .build();
    }
}
