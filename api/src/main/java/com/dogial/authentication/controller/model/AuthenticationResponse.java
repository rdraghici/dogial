package com.dogial.authentication.controller.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
