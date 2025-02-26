package com.dogial.dog.controller;

import com.dogial.dog.controller.model.DogRequest;
import com.dogial.dog.controller.model.DogResponse;
import com.dogial.dog.service.DogService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.UUID;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/v1/dogs")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @Post
    public HttpResponse<DogResponse> createDog(@Body DogRequest dogRequest) {
        return dogService.createDog(dogRequest);
    }

    @Put("/{id}")
    @Patch("/{id}")
    public HttpResponse<DogResponse> updateDog(@PathVariable UUID id, @Body DogRequest dogRequest) {
        return dogService.updateDog(id, dogRequest);
    }

    @Delete("/{id}")
    public HttpResponse<Void> deleteDog(@PathVariable UUID id) {
        return dogService.deleteDog(id);
    }

    @Get("/{id}")
    public HttpResponse<DogResponse> getDog(@PathVariable UUID id) {
        return dogService.getDog(id);
    }
}