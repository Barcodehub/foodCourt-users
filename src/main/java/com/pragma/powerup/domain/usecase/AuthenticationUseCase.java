package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IAuthenticationServicePort;
import com.pragma.powerup.domain.model.auth.AuthResponse;
import com.pragma.powerup.domain.model.auth.LoginRequest;
import com.pragma.powerup.domain.spi.IAuthenticationPort;

public class AuthenticationUseCase implements IAuthenticationServicePort {

    private final IAuthenticationPort authenticationPort;

    public AuthenticationUseCase(IAuthenticationPort authenticationPort) {
        this.authenticationPort = authenticationPort;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        return authenticationPort.authenticate(loginRequest);
    }
}
