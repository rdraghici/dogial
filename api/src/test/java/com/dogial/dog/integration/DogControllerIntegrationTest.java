package com.dogial.dog.integration;

import com.dogial.boot.IntegrationTestBase;
import com.dogial.dog.controller.model.DogRequest;
import com.dogial.dog.controller.model.DogResponse;
import com.dogial.dog.persistence.dao.DogDao;
import com.dogial.dog.persistence.entities.DogEntity;
import com.dogial.user.persistence.dao.UserDao;
import com.dogial.user.persistence.entities.UserEntity;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static io.micronaut.http.HttpRequest.POST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DogControllerIntegrationTest extends IntegrationTestBase {

    public static final String SERVICE_PATH = "/v1/dogs";

    @Inject
    @Client("/")
    protected HttpClient client;

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

    @Test
    void testCreateDog_returnsSuccess() {
        UUID ownerId = UUID.randomUUID();
        DogRequest dogRequest = new DogRequest(ownerId, "Buddy", "Labrador", "Male",
                new BigDecimal("30.5"), "2 years", true, "Friendly", true);
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

        MutableHttpRequest<DogRequest> request = POST(SERVICE_PATH, dogRequest);
        HttpResponse<DogResponse> response = client.toBlocking().exchange(request, Argument.of(DogResponse.class));

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertEquals(dogRequest.name(), response.body().name());
    }

    @Test
    void testUpdateDog_returnsSuccess() {
        UUID dogId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        DogRequest dogRequest = new DogRequest(ownerId, "Buddy", "Labrador", "Male",
                new BigDecimal("30.5"), "2 years", true, "Friendly", true);
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

        MutableHttpRequest<DogRequest> request = HttpRequest.PUT(SERVICE_PATH + "/" + dogId, dogRequest);
        HttpResponse<DogResponse> response = client.toBlocking().exchange(request, Argument.of(DogResponse.class));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(dogRequest.name(), response.body().name());
    }

    @Test
    void testDeleteDog_returnsSuccess() {
        UUID dogId = UUID.randomUUID();
        DogEntity dogEntity = DogEntity.builder().id(dogId).build();
        when(dogDao.findById(any())).thenReturn(Optional.of(dogEntity));
        doNothing().when(dogDao).delete(any());

        MutableHttpRequest<Object> request = HttpRequest.DELETE(SERVICE_PATH + "/" + dogId);
        HttpResponse<Void> response = client.toBlocking().exchange(request, Argument.VOID);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void testGetDog_returnsSuccess() {
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

        MutableHttpRequest<Object> request = HttpRequest.GET(SERVICE_PATH + "/" + dogId);
        HttpResponse<DogResponse> response = client.toBlocking().exchange(request, Argument.of(DogResponse.class));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(dogEntity.getName(), response.body().name());
    }

    @Test
    void testUpdateDog_returnsNotFound() {
        UUID dogId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        DogRequest dogRequest = new DogRequest(ownerId, "Buddy", "Labrador", "Male",
                new BigDecimal("30.5"), "2 years", true, "Friendly", true);
        when(dogDao.findById(any())).thenReturn(Optional.empty());

        MutableHttpRequest<DogRequest> request = HttpRequest.PUT(SERVICE_PATH + "/" + dogId, dogRequest);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Argument.of(DogResponse.class));
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testDeleteDog_returnsNotFound() {
        UUID dogId = UUID.randomUUID();
        when(dogDao.findById(any())).thenReturn(Optional.empty());

        MutableHttpRequest<Object> request = HttpRequest.DELETE(SERVICE_PATH + "/" + dogId);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Argument.VOID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetDog_returnsNotFound() {
        UUID dogId = UUID.randomUUID();
        when(dogDao.findById(any())).thenReturn(Optional.empty());

        MutableHttpRequest<Object> request = HttpRequest.GET(SERVICE_PATH + "/" + dogId);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Argument.of(DogResponse.class));
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}