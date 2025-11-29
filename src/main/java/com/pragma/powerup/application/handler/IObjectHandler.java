package com.pragma.powerup.application.handler;

import com.pragma.powerup.apifirst.model.ObjectRequestDto;
import com.pragma.powerup.apifirst.model.ObjectResponseDto;

import java.util.List;

public interface IObjectHandler {

    void saveObject(ObjectRequestDto objectRequestDto);

    List<ObjectResponseDto> getAllObjects();
}
