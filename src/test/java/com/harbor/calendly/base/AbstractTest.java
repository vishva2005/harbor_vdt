package com.harbor.calendly.base;

import com.harbor.calendly.model.AvailabilityDto.WEEKDAY;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

;import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected RequestSpecification requestSpecification;

    @BeforeEach
    public void setup() {
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
                                                    WEEKDAY weekday, Integer startTime, Integer endTime,
                                                    Long startTimeEpoch, Long endTimeEpoch, boolean isAvailable) {
        jdbcTemplate.update("insert into availability_tbl(schedule_id, weekday, start_time_in_sec, end_time_in_sec, " +
                "start_date_time_in_epoch, end_date_time_in_epoch, is_available) " +
                "values(?,?,?,?,?,?,?)", scheduleId, Optional.ofNullable(weekday).map(WEEKDAY::name).orElse(null),
                startTime, endTime, startTimeEpoch, endTimeEpoch, isAvailable);
    }

}
