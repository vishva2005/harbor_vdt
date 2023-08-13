package com.harbor.calendly.controller;

import com.harbor.calendly.base.AbstractTest;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.model.AvailableSlotDto;
import com.harbor.calendly.model.BookingSlotDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.next;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingControllerTest extends AbstractTest {

    @ParameterizedTest
    @CsvSource({
            "MONDAY,6,10",
            "MONDAY,2,4",
            "MONDAY,10,12",
            "TUESDAY,4,5",
            "SATURDAY,2,5",
            "SATURDAY,13,17",
            "SATURDAY,16,17",
            "SATURDAY,4,5"
    })
    public void bookSlot_slotOutsideAvailableHours_returnsErrorCode(DayOfWeek bookingDay, int startHr, int endHr) {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        createAvailability(scheduleId, DayOfWeek.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(15,0),true);
        createAvailability(scheduleId, getLocalDateTime(DayOfWeek.MONDAY, 3), getLocalDateTime(DayOfWeek.MONDAY, 9), true);
        createAvailability(scheduleId, getLocalDateTime(DayOfWeek.SATURDAY, 4), getLocalDateTime(DayOfWeek.SATURDAY, 5), false);

        LocalDateTime startDateTime = LocalDate.now().with(next(bookingDay)).atTime(startHr, 0);
        LocalDateTime endDateTime = LocalDate.now().with(next(bookingDay)).atTime(endHr, 0);

        BookingSlotDto bookingSlotDto = BookingSlotDto.builder()
                .description("create new booking")
                .guestEmail("thrall@xyz.com")
                .guestName("Thrall")
                .startDateTimeInEpoch(startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .endDateTimeInEpoch(endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .build();

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("scheduleId", scheduleId)
                .body(bookingSlotDto)
                .post("/schedules/{scheduleId}/slots")
                .then()
                .statusCode(ErrorCode.SLOT_OUTSIDE_AVAILABLE_HOURS.getHttpStatusCode())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.SLOT_OUTSIDE_AVAILABLE_HOURS.name()));
    }

    @ParameterizedTest
    @CsvSource({
            "SATURDAY,5,6,SATURDAY,4,6",
            "SATURDAY,5,6,SATURDAY,5,7",
            "SATURDAY,5,6,SATURDAY,5,6",
            "MONDAY,5,6,MONDAY,5,6",
            "MONDAY,5,6,MONDAY,4,6",
            "MONDAY,5,6,MONDAY,5,7",
    })
    public void bookSlot_slotAlreadyBooked_returnsErrorCode(DayOfWeek bookedDay, int bookedStartHr, int bookedEndHr,
                                                            DayOfWeek dayToBook, int startHrToBook, int endHrToBook) {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");
        createBookingSlot(userId, getLocalDateTime(bookedDay, bookedStartHr), getLocalDateTime(bookedDay, bookedEndHr));

        createAvailability(scheduleId, DayOfWeek.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(15,0),true);
        createAvailability(scheduleId, getLocalDateTime(DayOfWeek.MONDAY, 3), getLocalDateTime(DayOfWeek.MONDAY, 9), true);

        LocalDateTime startDateTime = getLocalDateTime(dayToBook, startHrToBook);
        LocalDateTime endDateTime = getLocalDateTime(dayToBook, endHrToBook);

        BookingSlotDto bookingSlotDto = BookingSlotDto.builder()
                .description("create new booking")
                .guestEmail("thrall@xyz.com")
                .guestName("Thrall")
                .startDateTimeInEpoch(startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .endDateTimeInEpoch(endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .build();

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("scheduleId", scheduleId)
                .body(bookingSlotDto)
                .post("/schedules/{scheduleId}/slots")
                .then()
                .statusCode(ErrorCode.SLOT_ALREADY_BOOKED.getHttpStatusCode())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.SLOT_ALREADY_BOOKED.name()));
    }

    @ParameterizedTest
    @CsvSource({
            "SATURDAY,5,6,SATURDAY,4,5",
            "SATURDAY,5,6,SATURDAY,6,7",
            "MONDAY,5,6,MONDAY,4,5",
            "MONDAY,5,6,MONDAY,6,7",
    })
    public void bookSlot_slotAvailable_returnSuccess(DayOfWeek bookedDay, int bookedStartHr, int bookedEndHr,
                                                     DayOfWeek dayToBook, int startHrToBook, int endHrToBook) {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");
        createBookingSlot(userId, getLocalDateTime(bookedDay, bookedStartHr), getLocalDateTime(bookedDay, bookedEndHr));

        createAvailability(scheduleId, DayOfWeek.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(15,0),true);
        createAvailability(scheduleId, getLocalDateTime(DayOfWeek.MONDAY, 3), getLocalDateTime(DayOfWeek.MONDAY, 9), true);

        LocalDateTime startDateTime = getLocalDateTime(dayToBook, startHrToBook);
        LocalDateTime endDateTime = getLocalDateTime(dayToBook, endHrToBook);

        BookingSlotDto bookingSlotDto = BookingSlotDto.builder()
                .description("create new booking")
                .guestEmail("thrall@xyz.com")
                .guestName("Thrall")
                .startDateTimeInEpoch(startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .endDateTimeInEpoch(endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .build();

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("scheduleId", scheduleId)
                .body(bookingSlotDto)
                .post("/schedules/{scheduleId}/slots")
                .then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(2, jdbcTemplate.queryForObject("select count(*) from booking_slot_tbl", Integer.class));
    }
    
    @Test
    public void getAvailableSlot_noAvailableSlot_returnsEmptyArray() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        createAvailability(scheduleId, DayOfWeek.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(15,0),true);
        createAvailability(scheduleId, getLocalDateTime(DayOfWeek.MONDAY, 3), getLocalDateTime(DayOfWeek.MONDAY, 9), true);

        LocalDateTime startDateTime = getLocalDateTime(DayOfWeek.TUESDAY, 4);
        LocalDateTime endDateTime = startDateTime.plusDays(2);

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("scheduleId", scheduleId)
                .queryParam("startDateTime", startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .queryParam("endDateTime", endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .get("/schedules/{scheduleId}/slots")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    @Test
    public void getAvailableSlot_slotAvailable_returnsSlot() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        createAvailability(scheduleId, DayOfWeek.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(15,0),true);
        createBookingSlot(userId, getLocalDateTime(DayOfWeek.SATURDAY, 5), getLocalDateTime(DayOfWeek.SATURDAY, 6));

        LocalDateTime startDateTime = getLocalDateTime(DayOfWeek.SATURDAY, 0);
        LocalDateTime endDateTime = startDateTime.plusDays(3);

        List<AvailableSlotDto> expectedSlots = new ArrayList<>();
        expectedSlots.add(new AvailableSlotDto(getEpochSeconds(DayOfWeek.SATURDAY,3), getEpochSeconds(DayOfWeek.SATURDAY,5)));
        expectedSlots.add(new AvailableSlotDto(getEpochSeconds(DayOfWeek.SATURDAY,6), getEpochSeconds(DayOfWeek.SATURDAY,15)));

        List<AvailableSlotDto> actualSlots = requestSpecification.contentType(ContentType.JSON)
                .pathParam("scheduleId", scheduleId)
                .queryParam("startDateTime", startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .queryParam("endDateTime", endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .get("/schedules/{scheduleId}/slots")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().jsonPath().getList(".", AvailableSlotDto.class);

        assertEquals(expectedSlots.size(), actualSlots.size());
        for(int i = 0; i < expectedSlots.size(); i++) {
            AvailableSlotDto expectedSlot = expectedSlots.get(i);
            AvailableSlotDto actualSlot = actualSlots.get(i);
            assertEquals(expectedSlot, actualSlot);
        }
    }

    private LocalDateTime getLocalDateTime(DayOfWeek dayOfWeek, int hour) {
        return LocalDate.now().with(next(dayOfWeek)).atTime(hour, 0);
    }

    private long getEpochSeconds(DayOfWeek dayOfWeek, int hour) {
        return LocalDate.now().with(next(dayOfWeek)).atTime(hour, 0).atZone(ZoneId.systemDefault()).toEpochSecond();
    }

}
