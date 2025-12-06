package com.pragma.powerup.infrastructure.input.rest;


import com.pragma.powerup.apifirst.api.UsersApi;
import com.pragma.powerup.apifirst.model.UserDataResponseDto;
import com.pragma.powerup.apifirst.model.UserRequestDto;
import com.pragma.powerup.apifirst.model.UsersListResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.domain.enums.RoleEnum;
import com.pragma.powerup.infrastructure.security.annotations.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final IUserHandler userHandler;

    @RequireRole({RoleEnum.ADMINISTRADOR, RoleEnum.EMPLEADO, RoleEnum.PROPIETARIO})
    @Override
    public ResponseEntity<UserDataResponseDto> createUser(UserRequestDto userRequestDto) {
        UserDataResponseDto response = userHandler.createUser(userRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequireRole({RoleEnum.ADMINISTRADOR})
    @Override
    public ResponseEntity<UsersListResponseDto> getAllUsers(Integer page, Integer size) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        UsersListResponseDto response = userHandler.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @RequireRole({RoleEnum.ADMINISTRADOR, RoleEnum.EMPLEADO})
    @Override
    public ResponseEntity<UserDataResponseDto> getUserById(Long id) {
        UserDataResponseDto response = userHandler.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserDataResponseDto> publicRegisterUser(UserRequestDto userRequestDto) {
        UserDataResponseDto response = userHandler.createUser(userRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
