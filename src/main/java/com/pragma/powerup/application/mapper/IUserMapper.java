package com.pragma.powerup.application.mapper;

import com.pragma.powerup.apifirst.model.UserRequestDto;
import com.pragma.powerup.apifirst.model.UserResponseDto;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = {IRoleMapper.class})
public interface IUserMapper {

    UserModel toDomain(UserRequestDto userRequestDto);

    UserResponseDto toResponseDto(UserModel userModel);

}
