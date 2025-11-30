package com.pragma.powerup.infrastructure.out.security;

import com.pragma.powerup.domain.model.auth.AuthResponse;
import com.pragma.powerup.domain.model.auth.LoginRequest;
import com.pragma.powerup.domain.spi.IAuthenticationPort;
import com.pragma.powerup.infrastructure.security.jwt.JwtTokenProvider;
import com.pragma.powerup.infrastructure.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationAdapter implements IAuthenticationPort {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse authenticate(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String token = jwtTokenProvider.generateToken(
                    userDetails,
                    userDetails.getId(),
                    userDetails.getRole()
            );

            return new AuthResponse(
                    token,
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getRole()
            );

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }
}
