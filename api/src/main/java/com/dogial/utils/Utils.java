package com.dogial.utils;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

import java.util.UUID;

public class Utils {
    public void authorize(UUID entityUuid, UUID requestUuid) {
        if (!entityUuid.equals(requestUuid)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "UUID on the request does not match the UUID in the DB");
        }
    }
}