package com.pragma.powerup.application.mapper;

import com.pragma.powerup.apifirst.model.AuthDataResponseDto;
import com.pragma.powerup.apifirst.model.AuthResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para wrappers de respuesta de autenticación
 * Usa MapStruct para consistencia con el resto del proyecto
 */
@Mapper(componentModel = "spring")
public interface AuthResponseMapper {

    /**
     * Envuelve un AuthResponseDto en un wrapper AuthDataResponseDto
     */
    @Mapping(target = "data", source = "authResponseDto")
    AuthDataResponseDto toAuthDataResponse(AuthResponseDto authResponseDto);
}

