package com.harbor.calendly.base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;

;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected RequestSpecification requestSpecification;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("delete from booking_slot_tbl");
        jdbcTemplate.execute("delete from availability_tbl");
        jdbcTemplate.execute("delete from schedule_tbl");
        jdbcTemplate.execute("delete from users_tbl");
        this.requestSpecification = RestAssured.given()
                .baseUri("http://localhost:"+port);
    }

    protected Integer createUserAndReturnId(String name, String email) {
        jdbcTemplate.update("insert into users_tbl(name, email) values(?,?)", name, email);
        return jdbcTemplate.queryForObject("select id from users_tbl where email = ?", Integer.class, email);
    }

    protected Integer createScheduleAndReturnId(Integer userId, String name, String timeZone, String description) {
        jdbcTemplate.update("insert into schedule_tbl(user_id, name, timezone, description) " +
                "values(?,?,?,?)", userId, name, timeZone, description);
        return jdbcTemplate.queryForObject("select id from schedule_tbl where user_id = ? and name = ?",
                Integer.class, userId, name);
    }

    protected void createAvailability(Integer scheduleId,
                                      DayOfWeek weekday, Integer startTime, Integer endTime, boolean isAvailable) {
        jdbcTemplate.update("insert into availability_tbl(schedule_id, weekday, start_time_in_sec, duration_in_sec, " +
                "start_date_time_in_epoch, end_date_time_in_epoch, is_available) " +
                "values(?,?,?,?,?,?,?)", scheduleId, weekday.name(),
                startTime, endTime - startTime, null, null, isAvailable);
    }

    protected void createAvailability(Integer scheduleId,
                                      LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isAvailable) {
        long startDateTimeEpoch = startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        long endDateTimeEpoch = endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        jdbcTemplate.update("insert into availability_tbl(schedule_id, weekday, start_time_in_sec, duration_in_sec, " +
                        "start_date_time_in_epoch, end_date_time_in_epoch, is_available) " +
                        "values(?,?,?,?,?,?,?)", scheduleId, null, null, null, startDateTimeEpoch, endDateTimeEpoch, isAvailable);
    }

    protected void createBookingSlot(Integer userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        jdbcTemplate.update("insert into booking_slot_tbl(user_id, guest_email, guest_name, description, start_date_time_in_epoch, end_date_time_in_epoch) " +
                        "values(?,?,?,?,?,?)", userId, "Durotan@xyz.com", "Durotan", "war Meeting",
                startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond(), endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    protected Integer getSecondsFrom0Hour(Integer hour, Integer minutes) {
        if (hour == null) {
            return null;
        }
        return hour*3600 + minutes*60;
    }

}
