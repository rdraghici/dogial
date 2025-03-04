package com.dogial.user.service;

import com.dogial.authentication.service.PasswordEncoder;
import com.dogial.user.controller.model.UserRequest;
import com.dogial.user.controller.model.UserResponse;
import com.dogial.user.mapper.UserMapper;
import com.dogial.user.persistence.dao.UserDao;
import com.dogial.user.persistence.entities.UserEntity;
import io.micronaut.http.HttpResponse;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Singleton
@Slf4j
@Transactional
public class UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.userMapper = UserMapper.INSTANCE;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public HttpResponse<UserResponse> createUser(UserRequest userRequest) {
        if (userDao.existsByEmail(userRequest.email())) {
            log.error("User with email {} already exists", userRequest.email());
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        }
        UserEntity userEntity = userMapper.toEntity(userRequest);

        // Hash the password before saving
        userEntity.setPasswordHash(passwordEncoder.encode(userRequest.passwordHash()));

        UserEntity savedUser = userDao.save(userEntity);
        UserResponse userResponse = userMapper.toResponse(savedUser);
        return HttpResponse.created(userResponse);
    }

    @Transactional
    public HttpResponse<UserResponse> updateUser(UUID id, UserRequest userRequest) {
        Optional<UserEntity> existingUser = userDao.findById(id);
        if (existingUser.isPresent()) {
            UserEntity userEntity = existingUser.get();
            userEntity.setEmail(userRequest.email());
            userEntity.setPasswordHash(userRequest.passwordHash());
            userDao.update(userEntity);
            UserResponse userResponse = userMapper.toResponse(userEntity);
            return HttpResponse.ok(userResponse);
        } else {
            log.error("User with ID {} does not exist", id);
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }
    }

    @Transactional
    public HttpResponse<Void> deleteUser(UUID id) {
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isPresent()) {
            userDao.delete(userEntity.get());
            return HttpResponse.noContent();
        } else {
            log.error("User with ID {} does not exist", id);
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }
    }

    @Transactional
    public HttpResponse<UserResponse> getUser(UUID id) {
        Optional<UserEntity> user = userDao.findById(id);
        if (user.isPresent()) {
            return HttpResponse.ok(userMapper.toResponse(user.get()));
        } else {
            log.error("User with ID {} does not exist", id);
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }
    }
}