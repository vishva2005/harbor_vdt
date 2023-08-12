package com.harbor.calendly.controller;

import com.harbor.calendly.entities.User;
import com.harbor.calendly.model.UserDto;
import com.harbor.calendly.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto userDto) {
        User createdUser = userService.createUser(UserDto.transformToUser(userDto));
        return UserDto.transformToDto(createdUser);
    }

    @GetMapping(value = "/{userId}")
    public UserDto getUser(@PathVariable("userId") int userId) {
        return UserDto.transformToDto(userService.getUser(userId));
    }
}
