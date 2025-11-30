package com.pragma.powerup.application.mapper;

import com.pragma.powerup.apifirst.model.*;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.model.auth.AuthResponse;
import com.pragma.powerup.domain.model.auth.LoginRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = {IRoleMapper.class})
public interface IAuthMapper {

    LoginRequest toDto(LoginRequestDto userRequestDto);

    AuthResponseDto toResponseDto(AuthResponse userModel);
}
