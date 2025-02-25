package com.dogial.user.service;

import com.dogial.user.controller.model.UserRequest;
import com.dogial.user.persistence.dao.UserDao;
import com.dogial.user.persistence.entities.UserEntity;
import com.dogial.user.service.exception.UserServiceException;
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
    void testCreateUser_FailedWithException() {
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        when(userDao.existsByEmail(any())).thenReturn(false);
        when(userDao.save(any())).thenThrow(new RuntimeException("Database error"));

        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.createUser(userRequest);
        });

        assertEquals("Error creating user", exception.getMessage());
    }

    @Test
    void testUpdateUser_FailedWithException() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        when(userDao.findById(any())).thenReturn(Optional.of(new UserEntity()));
        when(userDao.update(any())).thenThrow(new RuntimeException("Database error"));

        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.updateUser(userId, userRequest);
        });

        assertEquals("Error updating user", exception.getMessage());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest("test@example.com", "passwordHash");
        when(userDao.findById(any())).thenReturn(Optional.empty());

        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.updateUser(userId, userRequest);
        });

        assertEquals("Error updating user", exception.getMessage());
    }

    @Test
    void testDeleteUser_FailedWithException() {
        UUID userId = UUID.randomUUID();
        when(userDao.findById(any())).thenReturn(Optional.of(new UserEntity()));
        doThrow(new UserServiceException("Error deleting user", new Exception())).when(userDao).delete(any());

        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("Error deleting user", exception.getMessage());
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

        UserServiceException exception = assertThrows(UserServiceException.class, () -> {
            userService.getUser(userId);
        });

        assertEquals("Error retrieving user", exception.getMessage());
    }
}