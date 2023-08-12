package com.harbor.calendly.controller;

import com.harbor.calendly.entities.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping
    public User createUser(User user) {
        return null;
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId")int userId) {
        return null;
    }
}
