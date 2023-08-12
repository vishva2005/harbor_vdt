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
                .contentType(ContentType.JSON)
                .post("/users")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("name", equalTo("Arthas"))
                .body("email", equalTo("arthas@xyz.com"))
                .body("id", notNullValue());
    }

    @Test
    public void testCreateUser_emailAlreadyExists_returnsError() {
        createUserAndReturnId("Arthas", "arthas@xyz.com");
        requestSpecification
                .body(UserDto.builder().name("Arthas").email("arthas@xyz.com").build())
                .contentType(ContentType.JSON)
                .post("/users")
                .then()
                .statusCode(HttpStatus.PRECONDITION_FAILED.value())
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.USER_ALREADY_EXISTS.name()));
    }

    @Test
    public void testGetUser_userExists_returnUserData() {
        Integer userId = createUserAndReturnId("Arthas", "arthas@xyz.com");

        requestSpecification
                .pathParam("userId", userId)
                .get("/users/{userId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
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
                .contentType(ContentType.JSON)
                .body("errorCode", equalTo(ErrorCode.USER_NOT_EXISTS.name()));
    }

}
