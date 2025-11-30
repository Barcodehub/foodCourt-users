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

/**
 * Handler de autenticación - Capa de aplicación
 * Responsabilidad: Coordinar entre el dominio y la presentación
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationHandler implements IAuthenticationHandler {

    private final IAuthenticationServicePort authenticationServicePort;
    private final IAuthMapper authMapper;
    private final AuthResponseMapper authResponseMapper;

    @Override
    public AuthDataResponseDto login(LoginRequestDto loginRequestDto) {
        // 1. Mapear DTO → Domain
        LoginRequest loginRequest = authMapper.toDto(loginRequestDto);

        // 2. Ejecutar lógica de autenticación
        AuthResponse authResponse = authenticationServicePort.login(loginRequest);

        // 3. Mapear Domain → DTO
        AuthResponseDto authResponseDto = authMapper.toResponseDto(authResponse);

        // 4. Envolver en response (delegado a AuthResponseMapper)
        return authResponseMapper.toAuthDataResponse(authResponseDto);
    }
}
