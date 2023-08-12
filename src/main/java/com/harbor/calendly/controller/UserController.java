package com.harbor.calendly.controller;

import com.harbor.calendly.model.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping
    public UserDto createUser(UserDto user) {
        return UserDto.builder().name("test").build();
    }

    @GetMapping(value = "/{userId}", produces = "application/json")
    public UserDto getUser(@PathVariable("userId")int userId) {
        return UserDto.builder().name("test").build();
    }
}
