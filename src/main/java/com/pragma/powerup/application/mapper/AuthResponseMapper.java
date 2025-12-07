package com.pragma.powerup.application.mapper;

import com.pragma.powerup.apifirst.model.AuthDataResponseDto;
import com.pragma.powerup.apifirst.model.AuthResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthResponseMapper {

    @Mapping(target = "data", source = "authResponseDto")
    AuthDataResponseDto toAuthDataResponse(AuthResponseDto authResponseDto);
}

