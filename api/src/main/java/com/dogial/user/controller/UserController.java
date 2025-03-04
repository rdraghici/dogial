package com.dogial.user.controller;

import com.dogial.user.controller.model.UserRequest;
import com.dogial.user.controller.model.UserResponse;
import com.dogial.user.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.UUID;

@Controller("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post
    public HttpResponse<UserResponse> createUser(@Body UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Put("/{id}")
    @Patch("/{id}")
    public HttpResponse<UserResponse> updateUser(@PathVariable UUID id, @Body UserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Delete("/{id}")
    public HttpResponse<Void> deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/{id}")
    public HttpResponse<UserResponse> getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }
}