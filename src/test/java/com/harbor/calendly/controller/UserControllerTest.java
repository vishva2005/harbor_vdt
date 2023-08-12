package com.harbor.calendly.controller;

import com.harbor.calendly.base.AbstractTest;
import com.harbor.calendly.errors.ErrorCode;
import com.harbor.calendly.model.UserDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserControllerTest extends AbstractTest {

    @Test
    public void testCreateUser_emailAndNameProvided_returnsSuccess() {
        requestSpecification
                .body(UserDto.builder().name("Arthas").email("arthas@xyz.com").build())
                .contentType(APPLICATION_JSON)
                .post("/users")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(APPLICATION_JSON)
                .body("name", equalTo("Arthas"))
                .body("email", equalTo("arthas@xyz.com"))
                .body("id", notNullValue());
    }

    @Test
    public void testCreateUser_emailAlreadyExists_returnsError() {
        jdbcTemplate.execute("insert into users_tbl(name, email) values('Arthas','arthas@xyz.com')");
        requestSpecification
                .body(UserDto.builder().name("Arthas").email("arthas@xyz.com").build())
                .contentType(ContentType.JSON)
                .post("/users")
                .then()
                .statusCode(HttpStatus.PRECONDITION_FAILED.value())
                .contentType(APPLICATION_JSON)
                .body("errorCode", equalTo(ErrorCode.USER_ALREADY_EXISTS.name()));
    }

    @Test
    public void testGetUser_userExists_returnUserData() {
        jdbcTemplate.execute("insert into users_tbl(name, email) values('Arthas','arthas@xyz.com')");
        int userId = jdbcTemplate.queryForObject("select id from users_tbl where email = 'arthas@xyz.com'",
                Integer.class);

        requestSpecification
                .pathParam("userId", userId)
                .get("/users/{userId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(APPLICATION_JSON)
                .body("name", equalTo("Arthas"))
                .body("email", equalTo("arthas@xyz.com"))
                .body("id", equalTo(userId));
    }

    @Test
    public void testGetUser_userNotExists_return404() {
        requestSpecification
                .pathParam("userId", 123)
                .get("/users/{userId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(APPLICATION_JSON)
                .body("errorCode", equalTo(ErrorCode.USER_NOT_EXISTS.name()));
    }

}
