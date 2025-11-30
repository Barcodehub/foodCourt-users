package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.apifirst.model.UserDataResponseDto;
import com.pragma.powerup.apifirst.model.UserRequestDto;
import com.pragma.powerup.apifirst.model.UserResponseDto;
import com.pragma.powerup.apifirst.model.UsersListResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.application.mapper.IUserMapper;
import com.pragma.powerup.application.mapper.ResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler de usuarios - Capa de aplicación
 * Responsabilidad: Coordinar entre el dominio y la presentación
 */
@Service
@AllArgsConstructor
public class UserHandler implements IUserHandler {

    private final IUserMapper userMapper;
    private final ResponseMapper responseMapper;
    private final IUserServicePort userServicePort;

    @Override
    public UserDataResponseDto createUser(UserRequestDto userRequestDto) {
        UserModel userModel = userMapper.toDomain(userRequestDto);

        UserModel savedUser = userServicePort.createUser(userModel);

        UserResponseDto userResponseDto = userMapper.toResponseDto(savedUser);

        return responseMapper.toUserDataResponse(userResponseDto);
    }

    @Override
    public UsersListResponseDto getAllUsers(Pageable pageable) {
        Page<UserModel> userPage = userServicePort.getAllUsers(pageable);

        List<UserResponseDto> userResponseDtos = userPage.getContent().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());

        return responseMapper.toUsersListResponse(userResponseDtos, userPage);
    }

    @Override
    public UserDataResponseDto getUserById(Long id) {
        UserModel userModel = userServicePort.getUserById(id);

        UserResponseDto userResponseDto = userMapper.toResponseDto(userModel);

        return responseMapper.toUserDataResponse(userResponseDto);
    }
}
