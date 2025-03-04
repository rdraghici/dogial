package com.dogial.authentication.controller;

import com.dogial.authentication.controller.model.AuthenticationRequest;
import com.dogial.authentication.controller.model.AuthenticationResponse;
import com.dogial.authentication.service.AuthenticationService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Inject
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Post("/login")
    public HttpResponse<AuthenticationResponse> login(@Body AuthenticationRequest request) {
        return authenticationService.authenticate(request);
    }
}