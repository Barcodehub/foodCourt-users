package com.pragma.powerup.application.handler;

import com.pragma.powerup.apifirst.model.AuthDataResponseDto;
import com.pragma.powerup.apifirst.model.LoginRequestDto;

public interface IAuthenticationHandler {

    AuthDataResponseDto login(LoginRequestDto loginRequest);

}
