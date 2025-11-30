package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.apifirst.model.UserRequestDto;
import com.pragma.powerup.apifirst.model.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.application.mapper.IUserMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserHandler implements IUserHandler {

    private final IUserMapper userMapper;
    private final IUserServicePort userServicePort;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        UserModel userModel = userMapper.toDto(userRequestDto);
        UserModel savedUser = userServicePort.createUser(userModel);
        return userMapper.toResponseDto(savedUser);
    }
}
