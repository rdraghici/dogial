package com.dogial.dog.service;

import com.dogial.dog.controller.model.DogRequest;
import com.dogial.dog.controller.model.DogResponse;
import com.dogial.dog.persistence.dao.DogDao;
import com.dogial.dog.persistence.entities.DogEntity;
import com.dogial.user.persistence.dao.UserDao;
import com.dogial.user.persistence.entities.UserEntity;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest(rebuildContext = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DogServiceTest {

    @Inject
    private DogDao dogDao;

    @Inject
    private UserDao userDao;

    @MockBean(DogDao.class)
    DogDao dogDao() {
        return mock(DogDao.class);
    }

    @MockBean(UserDao.class)
    UserDao userDao() {
        return mock(UserDao.class);
    }

    @Inject
    private DogService dogService;

    @Test
    void testCreateDog_Success() {
        UUID ownerId = UUID.randomUUID();
        DogRequest dogRequest = new DogRequest(ownerId, "Buddy", "Labrador", "Male", new BigDecimal("30.5"), "2 years", true, "Friendly", true);
        DogEntity dogEntity = DogEntity.builder()
                .id(UUID.randomUUID())
                .owner(UserEntity.builder().id(ownerId).build())
                .name(dogRequest.name())
                .breed(dogRequest.breed())
                .gender(dogRequest.gender())
                .weight(dogRequest.weight())
                .age(dogRequest.age())
                .isNeutered(dogRequest.isNeutered())
                .behavior(dogRequest.behavior())
                .pedigree(dogRequest.pedigree())
                .build();
        when(userDao.findById(any())).thenReturn(Optional.of(UserEntity.builder().id(ownerId).build()));
        when(dogDao.save(any())).thenReturn(dogEntity);

        HttpResponse<DogResponse> response = dogService.createDog(dogRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertEquals(dogRequest.name(), response.body().name());
    }

    @Test
    void testUpdateDog_Success() {
        UUID dogId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        DogRequest dogRequest = new DogRequest(ownerId, "Buddy", "Labrador", "Male", new BigDecimal("30.5"), "2 years", true, "Friendly", true);
        DogEntity dogEntity = DogEntity.builder()
                .id(dogId)
                .owner(UserEntity.builder().id(ownerId).build())
                .name(dogRequest.name())
                .breed(dogRequest.breed())
                .gender(dogRequest.gender())
                .weight(dogRequest.weight())
                .age(dogRequest.age())
                .isNeutered(dogRequest.isNeutered())
                .behavior(dogRequest.behavior())
                .pedigree(dogRequest.pedigree())
                .build();
        when(dogDao.findById(any())).thenReturn(Optional.of(dogEntity));
        when(userDao.findById(any())).thenReturn(Optional.of(UserEntity.builder().id(ownerId).build()));
        when(dogDao.update(any())).thenReturn(dogEntity);

        HttpResponse<DogResponse> response = dogService.updateDog(dogId, dogRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(dogRequest.name(), response.body().name());
    }

    @Test
    void testDeleteDog_Success() {
        UUID dogId = UUID.randomUUID();
        DogEntity dogEntity = DogEntity.builder().id(dogId).build();
        when(dogDao.findById(any())).thenReturn(Optional.of(dogEntity));
        doNothing().when(dogDao).delete(any());

        HttpResponse<Void> response = dogService.deleteDog(dogId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void testGetDog_Success() {
        UUID dogId = UUID.randomUUID();
        DogEntity dogEntity = DogEntity.builder()
                .id(dogId)
                .name("Buddy")
                .breed("Labrador")
                .gender("Male")
                .weight(new BigDecimal("30.5"))
                .age("2 years")
                .isNeutered(true)
                .behavior("Friendly")
                .pedigree(true)
                .build();
        when(dogDao.findById(any())).thenReturn(Optional.of(dogEntity));

        HttpResponse<DogResponse> response = dogService.getDog(dogId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(dogEntity.getName(), response.body().name());
    }

    @Test
    void testUpdateDog_DogNotFound() {
        UUID dogId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        DogRequest dogRequest = new DogRequest(ownerId, "Buddy", "Labrador", "Male", new BigDecimal("30.5"), "2 years", true, "Friendly", true);
        when(dogDao.findById(any())).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> {
            dogService.updateDog(dogId, dogRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Dog does not exist", exception.getMessage());
    }

    @Test
    void testDeleteDog_DogNotFound() {
        UUID dogId = UUID.randomUUID();
        when(dogDao.findById(any())).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> {
            dogService.deleteDog(dogId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Dog does not exist", exception.getMessage());
    }

    @Test
    void testGetDog_DogNotFound() {
        UUID dogId = UUID.randomUUID();
        when(dogDao.findById(any())).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> {
            dogService.getDog(dogId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Dog does not exist", exception.getMessage());
    }
}