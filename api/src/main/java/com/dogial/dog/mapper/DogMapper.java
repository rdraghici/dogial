package com.dogial.dog.mapper;

import com.dogial.dog.controller.model.DogRequest;
import com.dogial.dog.controller.model.DogResponse;
import com.dogial.dog.persistence.entities.DogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DogMapper {
    DogMapper INSTANCE = Mappers.getMapper(DogMapper.class);

    DogEntity toEntity(DogRequest dogRequest);

    DogResponse toResponse(DogEntity dogEntity);
}