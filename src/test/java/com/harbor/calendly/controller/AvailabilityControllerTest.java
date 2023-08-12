package com.harbor.calendly.controller;

import com.harbor.calendly.base.AbstractTest;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.model.AvailabilityDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.harbor.calendly.model.AvailabilityDto.WEEKDAY;
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
                        .weekDay(WEEKDAY.SUNDAY)
                        .startTimeInSec(getSecondsFrom0Hour(8, 30))
                        .endTimeInSec(getSecondsFrom0Hour(9, 30))
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
    public void setAvailability_badData_returnsBadRequest(WEEKDAY weekDay, Integer startHr, Integer endHr, Long startEpoch, Long endEpoch) {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        List<AvailabilityDto> availibilityList = new ArrayList<>();
        availibilityList.add(AvailabilityDto.builder()
                .weekDay(WEEKDAY.SUNDAY)
                .startTimeInSec(getSecondsFrom0Hour(8, 30))
                .endTimeInSec(getSecondsFrom0Hour(9, 30))
                .build());
        availibilityList.add(AvailabilityDto.builder()
                .weekDay(weekDay)
                .startTimeInSec(getSecondsFrom0Hour(startHr, 0))
                .endTimeInSec(getSecondsFrom0Hour(endHr, 0))
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
    public void setAvailability_availabilityInWeeks_returnsSuccess(WEEKDAY weekDay, Integer startHr, Integer endHr, Long startEpoch,
                                                                   Long endEpoch, boolean isAvailable) {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        List<AvailabilityDto> availibilityList = new ArrayList<>();
        availibilityList.add(AvailabilityDto.builder()
                .weekDay(weekDay)
                .startTimeInSec(getSecondsFrom0Hour(startHr, 0))
                .endTimeInSec(getSecondsFrom0Hour(endHr, 0))
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

        createAvailability(scheduleId, WEEKDAY.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(4,0),
                null, null, true);
        createAvailability(scheduleId, WEEKDAY.SATURDAY, getSecondsFrom0Hour(13,0), getSecondsFrom0Hour(14,0),
                null, null, true);

        List<AvailabilityDto> availibilityList = new ArrayList<>();
        availibilityList.add(AvailabilityDto.builder()
                .weekDay(WEEKDAY.SUNDAY)
                .startTimeInSec(getSecondsFrom0Hour(1, 0))
                .endTimeInSec(getSecondsFrom0Hour(2, 0))
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

        createAvailability(scheduleId, WEEKDAY.SATURDAY, getSecondsFrom0Hour(3,0), getSecondsFrom0Hour(4,0),
                null, null, true);

        Calendar endDateTime = Calendar.getInstance();
        endDateTime.add(Calendar.HOUR, 1);

        createAvailability(scheduleId, null, null, null,
                Calendar.getInstance().getTimeInMillis()/1000,
                endDateTime.getTimeInMillis()/1000, true);

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

    private static final Integer getSecondsFrom0Hour(Integer hour, Integer minutes) {
        if (hour == null) {
            return null;
        }
        return hour*3600 + minutes*60;
    }

}
