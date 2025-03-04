package com.dogial.user.integration;

import com.dogial.boot.IntegrationTestBase;
import com.dogial.user.controller.model.UserRequest;
import com.dogial.user.controller.model.UserResponse;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.micronaut.http.HttpRequest.POST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest extends IntegrationTestBase {

    public static final String SERVICE_PATH = "/v1/users";

    @Inject
    @Client("/")
    protected HttpClient client;

    @Inject
    private UserDao userDao;

    @MockBean(UserDao.class)
    UserDao userDao() {
        return mock(UserDao.class);
    }

    @Test
    void testCreateUser_returnsSuccess() {
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .email(userRequest.email())
                .passwordHash(userRequest.passwordHash())
                .build();
        when(userDao.existsByEmail(any())).thenReturn(false);
        when(userDao.save(any())).thenReturn(userEntity);

        MutableHttpRequest<UserRequest> request = POST(SERVICE_PATH, userRequest);
        HttpResponse<UserResponse> response = client.toBlocking().exchange(request, Argument.of(UserResponse.class));

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertEquals(userRequest.email(), response.body().email());
    }

    @Test
    void testCreateUser_returnsBadRequest() {
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        when(userDao.existsByEmail(any())).thenReturn(true);

        MutableHttpRequest<UserRequest> request = POST(SERVICE_PATH, userRequest);
        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Argument.of(UserResponse.class));
        });


        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testUpdateUser_returnsSuccess() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest("updated@example.com", "newPasswordHash");
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .email(userRequest.email())
                .passwordHash(userRequest.passwordHash())
                .build();
        when(userDao.findById(any())).thenReturn(Optional.of(userEntity));
        when(userDao.update(any())).thenReturn(userEntity);

        // Add authentication
        MutableHttpRequest<UserRequest> request = HttpRequest.PUT(SERVICE_PATH + "/" + userId, userRequest)
                .header("Authorization", bearerAuth(TEST_EMAIL, List.of("ROLE_USER")));

        HttpResponse<UserResponse> response = client.toBlocking().exchange(request, Argument.of(UserResponse.class));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(userRequest.email(), response.body().email());
    }

    @Test
    void testUpdateUser_unauthorized() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest("updated@example.com", "newPasswordHash");

        // No auth header
        MutableHttpRequest<UserRequest> request = HttpRequest.PUT(SERVICE_PATH + "/" + userId, userRequest);

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Argument.of(UserResponse.class));
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void testUpdateUser_returnsNotFound() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest("updated@example.com", "newPasswordHash");
        when(userDao.findById(any())).thenReturn(Optional.empty());

        MutableHttpRequest<UserRequest> request = HttpRequest.PUT(SERVICE_PATH + "/" + userId, userRequest)
                .header("Authorization", bearerAuth(TEST_EMAIL, List.of("ROLE_USER")));

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Argument.of(UserResponse.class));
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testDeleteUser_returnsSuccess() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userDao.findById(any())).thenReturn(Optional.of(userEntity));
        doNothing().when(userDao).delete(any());

        MutableHttpRequest<Object> request = HttpRequest.DELETE(SERVICE_PATH + "/" + userId)
                .header("Authorization", bearerAuth(TEST_EMAIL, List.of("ROLE_USER")));

        HttpResponse<Void> response = client.toBlocking().exchange(request, Argument.VOID);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void testDeleteUser_returnsNotFound() {
        UUID userId = UUID.randomUUID();
        when(userDao.findById(any())).thenReturn(Optional.empty());

        MutableHttpRequest<Object> request = HttpRequest.DELETE(SERVICE_PATH + "/" + userId)
                .header("Authorization", bearerAuth(TEST_EMAIL, List.of("ROLE_USER")));

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Argument.VOID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testGetUser_returnsSuccess() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .email("test@example.com")
                .passwordHash("passwordHash")
                .build();
        when(userDao.findById(any())).thenReturn(Optional.of(userEntity));

        MutableHttpRequest<Object> request = HttpRequest.GET(SERVICE_PATH + "/" + userId)
                .header("Authorization", bearerAuth(TEST_EMAIL, List.of("ROLE_USER")));

        HttpResponse<UserResponse> response = client.toBlocking().exchange(request, Argument.of(UserResponse.class));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(userEntity.getEmail(), response.body().email());
    }

    @Test
    void testGetUser_returnsNotFound() {
        UUID userId = UUID.randomUUID();
        when(userDao.findById(any())).thenReturn(Optional.empty());

        MutableHttpRequest<Object> request = HttpRequest.GET(SERVICE_PATH + "/" + userId)
                .header("Authorization", bearerAuth(TEST_EMAIL, List.of("ROLE_USER")));

        HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Argument.of(UserResponse.class));
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}