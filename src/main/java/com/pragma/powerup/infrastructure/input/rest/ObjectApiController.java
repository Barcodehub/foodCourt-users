package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.apifirst.api.ObjectsApi;
import com.pragma.powerup.apifirst.model.ObjectRequestDto;
import com.pragma.powerup.apifirst.model.ObjectResponseDto;
import com.pragma.powerup.application.handler.IObjectHandlerGenerated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Implementación del controlador REST para objetos.
 * Esta clase implementa la interfaz generada automáticamente por OpenAPI Generator.
 * Los DTOs (ObjectRequestDto, ObjectResponseDto) son generados automáticamente desde open-api.yaml
 */
@RestController
@RequiredArgsConstructor
public class ObjectApiController implements ObjectsApi {

    private final IObjectHandlerGenerated objectHandler;

    @Override
    public ResponseEntity<ObjectResponseDto> createObject(ObjectRequestDto objectRequestDto) {
        ObjectResponseDto response = objectHandler.createObject(objectRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<ObjectResponseDto>> getAllObjects() {
        List<ObjectResponseDto> objects = objectHandler.getAllObjects();
        return ResponseEntity.ok(objects);
    }

    @Override
    public ResponseEntity<ObjectResponseDto> getObjectById(Long id) {
        ObjectResponseDto response = objectHandler.getObjectById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ObjectResponseDto> updateObject(Long id, ObjectRequestDto objectRequestDto) {
        ObjectResponseDto response = objectHandler.updateObject(id, objectRequestDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteObject(Long id) {
        objectHandler.deleteObject(id);
        return ResponseEntity.noContent().build();
    }
}

