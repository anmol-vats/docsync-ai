package com.docsync.controller;

import com.docsync.dto.CreateUserRequest;
import com.docsync.dto.CreateUserResponse;
import com.docsync.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public CreateUserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }
}
