package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.apifirst.model.ObjectRequestDto;
import com.pragma.powerup.apifirst.model.ObjectResponseDto;
import com.pragma.powerup.application.handler.IObjectHandlerGenerated;
import com.pragma.powerup.application.mapper.IObjectApiMapper;
import com.pragma.powerup.domain.api.IObjectServicePort;
import com.pragma.powerup.domain.model.ObjectModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Implementación del handler de objetos usando DTOs generados automáticamente.
 * Los DTOs (ObjectRequestDto, ObjectResponseDto) son generados desde open-api.yaml
 * MapStruct se encarga del mapeo entre DTOs y modelos de dominio.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ObjectHandlerGenerated implements IObjectHandlerGenerated {

    private final IObjectServicePort objectServicePort;
    private final IObjectApiMapper objectApiMapper;

    @Override
    public ObjectResponseDto createObject(ObjectRequestDto objectRequestDto) {
        ObjectModel objectModel = objectApiMapper.toModel(objectRequestDto);
        objectModel.setCreatedAt(LocalDateTime.now());
        objectModel.setUpdatedAt(LocalDateTime.now());
        ObjectModel savedObject = objectServicePort.saveObject(objectModel);
        return objectApiMapper.toResponseDto(savedObject);
    }

    @Override
    public List<ObjectResponseDto> getAllObjects() {
        List<ObjectModel> objects = objectServicePort.getAllObjects();
        return objectApiMapper.toResponseDtoList(objects);
    }

    @Override
    public ObjectResponseDto getObjectById(Long id) {
        ObjectModel objectModel = objectServicePort.getObjectById(id);
        return objectApiMapper.toResponseDto(objectModel);
    }

    @Override
    public ObjectResponseDto updateObject(Long id, ObjectRequestDto objectRequestDto) {
        ObjectModel existingObject = objectServicePort.getObjectById(id);

        // Actualizar campos
        existingObject.setName(objectRequestDto.getName());
        existingObject.setDescription(objectRequestDto.getDescription());
        existingObject.setCategory(objectRequestDto.getCategory());
        existingObject.setActive(objectRequestDto.getActive());
        existingObject.setUpdatedAt(LocalDateTime.now());

        ObjectModel updatedObject = objectServicePort.saveObject(existingObject);
        return objectApiMapper.toResponseDto(updatedObject);
    }

    @Override
    public void deleteObject(Long id) {
        objectServicePort.deleteObject(id);
    }
}

