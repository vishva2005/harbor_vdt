package com.harbor.calendly.controller;

import com.harbor.calendly.base.AbstractTest;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.model.ScheduleDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ScheduleControllerTest extends AbstractTest {

    @Test
    public void createSchedule_successfulPersistence_returnPersistedInfo() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");

        ScheduleDto scheduleDto = ScheduleDto.builder()
                .name("schedule1").timezone("Asia/Kolkata").description("dummy")
                .build();
        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(scheduleDto)
                .post("/users/{userId}/schedules")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo(scheduleDto.getName()))
                .body("description", equalTo(scheduleDto.getDescription()))
                .body("timezone", equalTo(scheduleDto.getTimezone()));
    }

    @Test
    public void createSchedule_userNotExists_failedStatusCode() {
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .name("schedule1").timezone("Asia/Kolkata").description("dummy")
                .build();
        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", 123)
                .body(scheduleDto)
                .post("/users/{userId}/schedules")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.USER_NOT_EXISTS.name()));
    }

    @Test
    public void createSchedule_invalidTimeZone_failedStatusCode() {
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .name("schedule1").timezone("dummy").description("dummy")
                .build();
        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", 123)
                .body(scheduleDto)
                .post("/users/{userId}/schedules")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.INVALID_TIMEZONE.name()));
    }

    @Test
    public void getSchedule_scheduleNotExist_404StatusCode() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        requestSpecification
                .pathParam("userId", userId)
                .pathParam("scheduleId", 1234)
                .get("/users/{userId}/schedules/{scheduleId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.SCHEDULE_NOT_EXISTS.name()));
    }

    @Test
    public void getSchedule_scheduleExist_returnScheduleData() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        requestSpecification
                .pathParam("userId", userId)
                .pathParam("scheduleId", scheduleId)
                .get("/users/{userId}/schedules/{scheduleId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(scheduleId))
                .body("name", equalTo("schedule1"))
                .body("description", equalTo("dummy"))
                .body("timezone", equalTo("Asia/Kolkata"));
    }

    @Test
    public void deleteSchedule_scheduleNotExist_404StatusCode() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        requestSpecification
                .pathParam("userId", userId)
                .pathParam("scheduleId", 1234)
                .delete("/users/{userId}/schedules/{scheduleId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.SCHEDULE_NOT_EXISTS.name()));
    }

    @Test
    public void deleteSchedule_scheduleExist_return200() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        requestSpecification
                .pathParam("userId", userId)
                .pathParam("scheduleId", scheduleId)
                .delete("/users/{userId}/schedules/{scheduleId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void updateSchedule_scheduleNotExist_404StatusCode() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .name("schedule1").timezone("Asia/Kolkata").description("updated Dummy")
                .build();

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("scheduleId", 1234)
                .body(scheduleDto)
                .patch("/users/{userId}/schedules/{scheduleId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.SCHEDULE_NOT_EXISTS.name()));
    }

    @Test
    public void updateSchedule_scheduleExist_returnScheduleData() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");
        Integer scheduleId = createScheduleAndReturnId(userId, "schedule1", "Asia/Kolkata", "dummy");

        ScheduleDto scheduleDto = ScheduleDto.builder()
                .name("schedule1").timezone("Asia/Kolkata").description("updated Dummy")
                .build();

        requestSpecification.contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("scheduleId", scheduleId)
                .body(scheduleDto)
                .patch("/users/{userId}/schedules/{scheduleId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(scheduleId))
                .body("name", equalTo("schedule1"))
                .body("description", equalTo("updated Dummy"))
                .body("timezone", equalTo("Asia/Kolkata"));
    }

}
