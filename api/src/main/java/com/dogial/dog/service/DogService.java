package com.dogial.dog.service;

import com.dogial.dog.controller.model.DogRequest;
import com.dogial.dog.controller.model.DogResponse;
import com.dogial.dog.mapper.DogMapper;
import com.dogial.dog.persistence.dao.DogDao;
import com.dogial.dog.persistence.entities.DogEntity;
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
public class DogService {

    private final DogDao dogDao;
    private final UserDao userDao;
    private final DogMapper dogMapper;

    @Inject
    public DogService(DogDao dogDao, UserDao userDao) {
        this.dogDao = dogDao;
        this.userDao = userDao;
        this.dogMapper = DogMapper.INSTANCE;
    }

    @Transactional
    public HttpResponse<DogResponse> createDog(DogRequest dogRequest) {
        Optional<UserEntity> owner = userDao.findById(dogRequest.ownerId());
        if (owner.isEmpty()) {
            log.error("Owner with ID {} does not exist", dogRequest.ownerId());
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Owner does not exist");
        }
        DogEntity dogEntity = dogMapper.toEntity(dogRequest);
        dogEntity.setOwner(owner.get());
        DogEntity savedDog = dogDao.save(dogEntity);
        DogResponse dogResponse = dogMapper.toResponse(savedDog);
        return HttpResponse.created(dogResponse);
    }

    @Transactional
    public HttpResponse<DogResponse> updateDog(UUID id, DogRequest dogRequest) {
        Optional<DogEntity> existingDog = dogDao.findById(id);
        if (existingDog.isPresent()) {
            Optional<UserEntity> owner = userDao.findById(dogRequest.ownerId());
            if (owner.isEmpty()) {
                log.error("Owner with ID {} does not exist", dogRequest.ownerId());
                throw new HttpStatusException(HttpStatus.NOT_FOUND, "Owner does not exist");
            }
            DogEntity dogEntity = existingDog.get();
            dogEntity.setOwner(owner.get());
            dogEntity.setName(dogRequest.name());
            dogEntity.setBreed(dogRequest.breed());
            dogEntity.setGender(dogRequest.gender());
            dogEntity.setWeight(dogRequest.weight());
            dogEntity.setAge(dogRequest.age());
            dogEntity.setIsNeutered(dogRequest.isNeutered());
            dogEntity.setBehavior(dogRequest.behavior());
            dogEntity.setPedigree(dogRequest.pedigree());
            dogDao.update(dogEntity);
            DogResponse dogResponse = dogMapper.toResponse(dogEntity);
            return HttpResponse.ok(dogResponse);
        } else {
            log.error("Dog with ID {} does not exist", id);
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Dog does not exist");
        }
    }

    @Transactional
    public HttpResponse<Void> deleteDog(UUID id) {
        Optional<DogEntity> dogEntity = dogDao.findById(id);
        if (dogEntity.isPresent()) {
            dogDao.delete(dogEntity.get());
            return HttpResponse.noContent();
        } else {
            log.error("Dog with ID {} does not exist", id);
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Dog does not exist");
        }
    }

    @Transactional
    public HttpResponse<DogResponse> getDog(UUID id) {
        Optional<DogEntity> dog = dogDao.findById(id);
        if (dog.isPresent()) {
            return HttpResponse.ok(dogMapper.toResponse(dog.get()));
        } else {
            log.error("Dog with ID {} does not exist", id);
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Dog does not exist");
        }
    }
}