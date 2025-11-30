package com.pragma.powerup.infrastructure.input.rest;


import com.pragma.powerup.apifirst.api.UsersApi;
import com.pragma.powerup.apifirst.model.UserRequestDto;
import com.pragma.powerup.apifirst.model.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final IUserHandler userHandler;

    @Override
    public ResponseEntity<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        UserResponseDto response = userHandler.createUser(userRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
