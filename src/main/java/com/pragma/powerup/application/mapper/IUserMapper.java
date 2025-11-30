package com.pragma.powerup.application.mapper;

import com.pragma.powerup.apifirst.model.UserRequestDto;
import com.pragma.powerup.apifirst.model.UserResponseDto;
import com.pragma.powerup.domain.model.UserModel;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface IUserMapper {
    UserModel toDto(UserRequestDto userRequestDto);
    UserResponseDto toResponseDto(UserModel userModel);
}
