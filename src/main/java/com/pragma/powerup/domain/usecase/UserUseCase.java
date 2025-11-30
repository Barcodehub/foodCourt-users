package com.pragma.powerup.domain.usecase;


import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.enums.RoleEnum;
import com.pragma.powerup.domain.exception.UserUnderageException;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Period;

@RequiredArgsConstructor
public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IRolePersistencePort rolePersistencePort;

    private static final int MINIMUM_AGE = 18;


    @Override
    public UserModel createUser(UserModel userModel) {
        validateAge(userModel.getBirthDate());

        String encodedPassword = passwordEncoderPort.encode(userModel.getPassword());
        userModel.setPassword(encodedPassword);

        // Asignar siempre el rol propietario
        RoleModel role = rolePersistencePort.findById(RoleEnum.PROPIETARIO.getRoleId())
                .orElseThrow(() -> new NoDataFoundException("Rol propietario no encontrado. roleID: " + RoleEnum.PROPIETARIO.getRoleId()));
        userModel.setRole(role);

        return userPersistencePort.saveUser(userModel);
    }

    @Override
    public Page<UserModel> getAllUsers(Pageable pageable) {
        return userPersistencePort.findAll(pageable);
    }

    @Override
    public UserModel getUserById(Long id) {
        return userPersistencePort.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
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
