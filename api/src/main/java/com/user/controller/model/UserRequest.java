package com.user.controller.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record UserRequest(
        String email,
        String passwordHash)
{}