package com.user.controller;

import com.user.controller.model.UserRequest;
import com.user.controller.model.UserResponse;
import com.user.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.UUID;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Post
    public HttpResponse<UserResponse> createUser(@Body UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @Put("/{id}")
    @Patch("/{id}")
    public HttpResponse<UserResponse> updateUser(@PathVariable UUID id, @Body UserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    @Delete("/{id}")
    public HttpResponse<Void> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }

    @Get("/{id}")
    public HttpResponse<UserResponse> getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }
}