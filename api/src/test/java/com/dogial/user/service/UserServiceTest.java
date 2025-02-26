package com.dogial.user.service;

import com.dogial.user.controller.model.UserRequest;
import com.dogial.user.controller.model.UserResponse;
import com.dogial.user.persistence.dao.UserDao;
import com.dogial.user.persistence.entities.UserEntity;
import com.dogial.user.service.exception.UserServiceException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest(rebuildContext = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Inject
    private UserDao userDao;

    @MockBean(UserDao.class)
    UserDao userDao() {
        return mock(UserDao.class);
    }

    @Inject
    private UserService userService;

    @Test
    void testCreateUser_Success() {
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .email(userRequest.email())
                .passwordHash(userRequest.passwordHash())
                .build();
        when(userDao.existsByEmail(any())).thenReturn(false);
        when(userDao.save(any())).thenReturn(userEntity);

        HttpResponse<UserResponse> response = userService.createUser(userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.body());
        assertEquals(userRequest.email(), response.body().email());
    }

    @Test
    void testUpdateUser_Success() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest("updated@example.com", "newPasswordHash");
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .email(userRequest.email())
                .passwordHash(userRequest.passwordHash())
                .build();
        when(userDao.findById(any())).thenReturn(Optional.of(userEntity));
        when(userDao.update(any())).thenReturn(userEntity);

        HttpResponse<UserResponse> response = userService.updateUser(userId, userRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(userRequest.email(), response.body().email());
    }

    @Test
    void testDeleteUser_Success() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userDao.findById(any())).thenReturn(Optional.of(userEntity));
        doNothing().when(userDao).delete(any());

        HttpResponse<Void> response = userService.deleteUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void testGetUser_Success() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .email("test@example.com")
                .passwordHash("passwordHash")
                .build();
        when(userDao.findById(any())).thenReturn(Optional.of(userEntity));

        HttpResponse<UserResponse> response = userService.getUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.body());
        assertEquals(userEntity.getEmail(), response.body().email());
    }

    @Test
    void testCreateUser_UserAlreadyExists() {
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        when(userDao.existsByEmail(any())).thenReturn(true);

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> {
            userService.createUser(userRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void testCreateUser_FailedWithException() {
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        when(userDao.existsByEmail(any())).thenReturn(false);
        when(userDao.save(any())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(userRequest);
        });

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testUpdateUser_FailedWithException() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        when(userDao.findById(any())).thenReturn(Optional.of(new UserEntity()));
        when(userDao.update(any())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, userRequest);
        });

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        when(userDao.findById(any())).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> {
            userService.updateUser(userId, userRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void testDeleteUser_FailedWithException() {
        UUID userId = UUID.randomUUID();
        when(userDao.findById(any())).thenReturn(Optional.of(new UserEntity()));
        doThrow(new RuntimeException("Database error")).when(userDao).delete(any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testDeleteUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userDao.findById(any())).thenReturn(Optional.empty());

        HttpStatusException exception = assertThrows(HttpStatusException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void testGetUser_FailedWithException() {
        UUID userId = UUID.randomUUID();
        when(userDao.findById(any())).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUser(userId);
        });

        assertEquals("Database error", exception.getMessage());
    }
}