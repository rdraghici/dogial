package com.dogial.authentication.controller.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record AuthenticationRequest(
        String email,
        String password
) {}
