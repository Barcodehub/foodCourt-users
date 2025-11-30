package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.auth.AuthResponse;
import com.pragma.powerup.domain.model.auth.LoginRequest;

public interface IAuthenticationPort {

    AuthResponse authenticate(LoginRequest loginRequest);

}
