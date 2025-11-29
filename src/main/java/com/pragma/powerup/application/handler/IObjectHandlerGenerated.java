package com.pragma.powerup.application.handler;

import com.pragma.powerup.apifirst.model.ObjectRequestDto;
import com.pragma.powerup.apifirst.model.ObjectResponseDto;

import java.util.List;

/**
 * Interface del handler de objetos.
 * Utiliza los DTOs generados automáticamente desde open-api.yaml
 */
public interface IObjectHandlerGenerated {

    ObjectResponseDto createObject(ObjectRequestDto objectRequestDto);

    List<ObjectResponseDto> getAllObjects();

    ObjectResponseDto getObjectById(Long id);

    ObjectResponseDto updateObject(Long id, ObjectRequestDto objectRequestDto);

    void deleteObject(Long id);
}

