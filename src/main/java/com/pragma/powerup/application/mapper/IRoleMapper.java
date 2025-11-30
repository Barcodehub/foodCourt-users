package com.pragma.powerup.application.mapper;

import com.pragma.powerup.apifirst.model.RoleResponseDto;
import com.pragma.powerup.domain.model.RoleModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IRoleMapper {

    RoleResponseDto toResponseDto(RoleModel roleModel);

    RoleModel toModel(RoleResponseDto roleResponseDto);
}

