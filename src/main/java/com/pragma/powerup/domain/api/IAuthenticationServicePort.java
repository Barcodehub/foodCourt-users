package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.auth.AuthResponse;
import com.pragma.powerup.domain.model.auth.LoginRequest;

public interface IAuthenticationServicePort {

    AuthResponse login(LoginRequest loginRequest);

}
