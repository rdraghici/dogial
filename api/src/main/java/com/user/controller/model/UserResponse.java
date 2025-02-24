package com.user.controller.model;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

@Serdeable
public record UserResponse(
        UUID id,
        String email,
        String passwordHash)
{}
