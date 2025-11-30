package com.pragma.powerup.domain.usecase;


import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.exception.UserUnderageException;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@RequiredArgsConstructor
public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    private static final int MINIMUM_AGE = 18;

    @Override
    public UserModel createUser(UserModel userModel) {
        validateAge(userModel.getBirthDate());

        String encodedPassword = passwordEncoderPort.encode(userModel.getPassword());
        userModel.setPassword(encodedPassword);

        return userPersistencePort.saveUser(userModel);
    }


    private void validateAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        int age = Period.between(birthDate, today).getYears();

        if (age < MINIMUM_AGE) {
            throw new UserUnderageException(
                String.format("El usuario debe ser mayor de edad. Edad actual: %d años", age)
            );
        }
    }
}
