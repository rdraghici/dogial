package com.dogial.dog.controller.model;

import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.util.UUID;

@Serdeable
public record DogResponse(
        UUID id,
        UUID ownerId,
        String name,
        String breed,
        String gender,
        BigDecimal weight,
        String age,
        Boolean isNeutered,
        String behavior,
        Boolean pedigree)
{}