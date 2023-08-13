package com.harbor.calendly.controller;

import com.harbor.calendly.base.AbstractTest;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.model.AvailabilityDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvailabilityControllerTest extends AbstractTest {

    @Test
    public void setAvailability_scheduleNotExists_returns404() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("scheduleId", 123)
                .body(Arrays.asList(AvailabilityDto.builder()
                        .weekDay(DayOfWeek.SUNDAY)
                        .startTimeInSec(getSecondsFrom0Hour(8, 30))
                        .durationInSec(3600)
                        .build()))
                .put("/users/{userId}/schedules/{scheduleId}/availability")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.SCHEDULE_NOT_EXISTS.name()));
    }

    @ParameterizedTest
    @CsvSource({
            "SUNDAY,2,3,130000,14000",
            "SUNDAY,3,3,,",
            ",,,150000,14000",
    })
    public void setAvailability_badData_returnsBadRequest(DayOfWeek weekDay, Integer startHr, Integer endHr, Long startEpoch, Long endEpoch) {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        List<AvailabilityDto> availibilityList = new ArrayList<>();
        availibilityList.add(AvailabilityDto.builder()
                .weekDay(DayOfWeek.SUNDAY)
                .startTimeInSec(getSecondsFrom0Hour(8, 30))
                .durationInSec(3600)
                .build());
        availibilityList.add(AvailabilityDto.builder()
                .weekDay(weekDay)
                .startTimeInSec(getSecondsFrom0Hour(startHr, 0))
                .durationInSec(endHr == null ? null : (endHr - startHr)*3600)
                .startDateTimeInEpoch(startEpoch)
                .endDateTimeInEpoch(endEpoch)
                .build());

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("scheduleId", scheduleId)
                .body(availibilityList)
                .put("/users/{userId}/schedules/{scheduleId}/availability")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.INVALID_AVAILABILITY.name()));
    }

    @ParameterizedTest
    @CsvSource({
        "SUNDAY,2,3,,,true",
        "SUNDAY,2,3,,,false",
        ",,,12000,14000, true",
        ",,,12000,14000, false",
    })
    public void setAvailability_availabilityInWeeks_returnsSuccess(DayOfWeek weekDay, Integer startHr, Integer endHr, Long startEpoch,
                                                                   Long endEpoch, boolean isAvailable) {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        List<AvailabilityDto> availibilityList = new ArrayList<>();
        availibilityList.add(AvailabilityDto.builder()
                .weekDay(weekDay)
                .startTimeInSec(getSecondsFrom0Hour(startHr, 0))
                .durationInSec(endHr == null ? null : (endHr - startHr)*3600)
                .startDateTimeInEpoch(startEpoch)
                .endDateTimeInEpoch(endEpoch)
                .isAvailable(isAvailable)
                .build());

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("scheduleId", scheduleId)
                .body(availibilityList)
                .put("/users/{userId}/schedules/{scheduleId}/availability")
                .then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from availability_tbl where schedule_id = "+scheduleId, Integer.class));
    }

    @Test
    public void updateAvailability_availabilityInWeeks_returnsSuccess() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        createAvailability(scheduleId, DayOfWeek.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(4,0), true);
        createAvailability(scheduleId, DayOfWeek.SATURDAY, getSecondsFrom0Hour(13,0), getSecondsFrom0Hour(14,0), true);

        List<AvailabilityDto> availibilityList = new ArrayList<>();
        availibilityList.add(AvailabilityDto.builder()
                .weekDay(DayOfWeek.SUNDAY)
                .startTimeInSec(getSecondsFrom0Hour(1, 0))
                .durationInSec(3600)
                .isAvailable(true)
                .build());

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("scheduleId", scheduleId)
                .body(availibilityList)
                .put("/users/{userId}/schedules/{scheduleId}/availability")
                .then()
                .statusCode(HttpStatus.OK.value());

        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from availability_tbl where schedule_id = "+scheduleId, Integer.class));
    }

    @Test
    public void getAvailability_scheduleNotExist_return404() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("scheduleId", 123)
                .get("/users/{userId}/schedules/{scheduleId}/availability")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    @Test
    public void getAvailability_availabilityPresent_returnSuccess() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        createAvailability(scheduleId, DayOfWeek.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(4,0), true);

        Calendar endDateTime = Calendar.getInstance();
        endDateTime.add(Calendar.HOUR, 1);

        createAvailability(scheduleId, LocalDateTime.now(), LocalDateTime.now().plusHours(5), true);

        List<AvailabilityDto> list = requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("scheduleId", scheduleId)
                .get("/users/{userId}/schedules/{scheduleId}/availability")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .body().jsonPath().getList(".", AvailabilityDto.class);

        assertEquals(2, list.size());
    }

}
