package com.pragma.powerup.application.handler;

import com.pragma.powerup.apifirst.model.UserRequestDto;
import com.pragma.powerup.apifirst.model.UserResponseDto;

public interface IUserHandler {
    UserResponseDto createUser(UserRequestDto userRequestDto);
}
