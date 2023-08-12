package com.harbor.calendly.base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractTest {

    public static final String APPLICATION_JSON = "application/json";

    @LocalServerPort
    protected int port;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected RequestSpecification requestSpecification;

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute("truncate table users_tbl");
        this.requestSpecification = RestAssured.given()
                .baseUri("http://localhost:"+port);
    }

}
