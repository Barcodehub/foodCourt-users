package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.apifirst.model.AuthDataResponseDto;
import com.pragma.powerup.apifirst.model.AuthResponseDto;
import com.pragma.powerup.apifirst.model.LoginRequestDto;
import com.pragma.powerup.application.handler.IAuthenticationHandler;
import com.pragma.powerup.application.mapper.AuthResponseMapper;
import com.pragma.powerup.application.mapper.IAuthMapper;
import com.pragma.powerup.domain.api.IAuthenticationServicePort;
import com.pragma.powerup.domain.model.auth.AuthResponse;
import com.pragma.powerup.domain.model.auth.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationHandler implements IAuthenticationHandler {

    private final IAuthenticationServicePort authenticationServicePort;
    private final IAuthMapper authMapper;
    private final AuthResponseMapper authResponseMapper;

    @Override
    public AuthDataResponseDto login(LoginRequestDto loginRequestDto) {
        LoginRequest loginRequest = authMapper.toDto(loginRequestDto);
        AuthResponse authResponse = authenticationServicePort.login(loginRequest);
        AuthResponseDto authResponseDto = authMapper.toResponseDto(authResponse);

        return authResponseMapper.toAuthDataResponse(authResponseDto);
    }
}
