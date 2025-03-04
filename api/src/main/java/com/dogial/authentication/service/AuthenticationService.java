package com.dogial.authentication.service;

import com.dogial.authentication.controller.model.AuthenticationRequest;
import com.dogial.authentication.controller.model.AuthenticationResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class AuthenticationService {

    private final JwtTokenGenerator tokenGenerator;

    public AuthenticationService(JwtTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public HttpResponse<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        try {

            // Create claims map for token generation
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", request.email());
            claims.put("roles", Collections.singletonList("ROLE_USER"));

            // Generate token with the claims map
            Optional<String> tokenOptional = tokenGenerator.generateToken(claims);

            if (tokenOptional.isPresent()) {
                String accessToken = tokenOptional.get();
                AuthenticationResponse response = new AuthenticationResponse(
                        accessToken,
                        null, // Refresh token can be handled separately if needed
                        "Bearer"
                );

                return HttpResponse.ok(response);
            } else {
                return HttpResponse.unauthorized();
            }
        } catch (Exception e) {
            return HttpResponse.unauthorized();
        }
    }
}