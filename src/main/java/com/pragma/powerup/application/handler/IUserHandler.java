package com.pragma.powerup.application.handler;

import com.pragma.powerup.apifirst.model.UserDataResponseDto;
import com.pragma.powerup.apifirst.model.UserRequestDto;
import com.pragma.powerup.apifirst.model.UsersListResponseDto;
import org.springframework.data.domain.Pageable;

public interface IUserHandler {
    UserDataResponseDto createUser(UserRequestDto userRequestDto);

    UsersListResponseDto getAllUsers(Pageable pageable);

    UserDataResponseDto getUserById(Long id);
}
