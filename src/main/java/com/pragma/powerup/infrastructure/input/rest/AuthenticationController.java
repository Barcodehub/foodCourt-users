package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.apifirst.api.AuthApi;
import com.pragma.powerup.apifirst.model.AuthDataResponseDto;
import com.pragma.powerup.apifirst.model.LoginRequestDto;
import com.pragma.powerup.application.handler.IAuthenticationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController implements AuthApi {

    private final IAuthenticationHandler authenticationHandler;

    @Override
    public ResponseEntity<AuthDataResponseDto> login(LoginRequestDto loginRequestDto) {
        AuthDataResponseDto response = authenticationHandler.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }
}
