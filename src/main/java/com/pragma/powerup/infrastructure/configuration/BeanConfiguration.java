package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IAuthenticationServicePort;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.spi.IAuthenticationPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.usecase.AuthenticationUseCase;
import com.pragma.powerup.domain.usecase.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IUserServicePort userServicePort(
            IUserPersistencePort userPersistencePort,
            IPasswordEncoderPort passwordEncoderPort,
            IRolePersistencePort rolePersistencePort) {
        return new UserUseCase(userPersistencePort, passwordEncoderPort, rolePersistencePort);
    }

    @Bean
    public IAuthenticationServicePort authenticationServicePort(IAuthenticationPort authenticationPort) {
        return new AuthenticationUseCase(authenticationPort);
    }
}

