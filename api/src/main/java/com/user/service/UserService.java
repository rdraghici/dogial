package com.user.service;

import com.user.controller.model.UserRequest;
import com.user.controller.model.UserResponse;
import com.user.mapper.UserMapper;
import com.user.persistence.dao.UserDao;
import com.user.persistence.entities.UserEntity;
import io.micronaut.http.HttpResponse;

import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
        this.userMapper = UserMapper.INSTANCE;
    }

    public HttpResponse<UserResponse> createUser(UserRequest userRequest) {
        if (userDao.existsByEmail(userRequest.email())) {
            return HttpResponse.badRequest();
        }
        UserEntity userEntity = userMapper.toEntity(userRequest);
        UserEntity savedUser = userDao.save(userEntity);
        UserResponse userResponse = userMapper.toResponse(savedUser);
        return HttpResponse.created(userResponse);
    }

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
            return HttpResponse.notFound();
        }
    }

    public HttpResponse<Void> deleteUser(UUID id) {
        Optional<UserEntity> userEntity = userDao.findById(id);
        if (userEntity.isPresent()) {
            userDao.delete(userEntity.get());
            return HttpResponse.noContent();
        } else {
            return HttpResponse.notFound();
        }
    }

    public HttpResponse<UserResponse> getUser(UUID id) {
        Optional<UserEntity> user = userDao.findById(id);
        return user.map(userEntity -> HttpResponse.ok(userMapper.toResponse(userEntity)))
                .orElseGet(HttpResponse::notFound);
    }
}