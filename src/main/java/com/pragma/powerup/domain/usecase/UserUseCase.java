package com.pragma.powerup.domain.usecase;


import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.enums.RoleEnum;
import com.pragma.powerup.domain.exception.UnauthorizedRoleCreationException;
import com.pragma.powerup.domain.exception.UserUnderageException;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
import com.pragma.powerup.domain.spi.ISecurityContextPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
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
    private final ISecurityContextPort securityContextPort;

    private static final int MINIMUM_AGE = 18;


    @Override
    public UserModel createUser(UserModel userModel) {
        RoleModel role = rolePersistencePort.findById(userModel.getRole().getId())
                .orElseThrow(() -> new NoDataFoundException("Rol no encontrado. roleID: " + userModel.getRole().getId()));
        userModel.setRole(role);

        if (!role.getId().equals(RoleEnum.CLIENTE.getRoleId())) {
            validateRoleCreationPermissions(role);
        }

        if (role.getId().equals(RoleEnum.PROPIETARIO.getRoleId())) {
            validateAge(userModel.getBirthDate());
        }

        String encodedPassword = passwordEncoderPort.encode(userModel.getPassword());
        userModel.setPassword(encodedPassword);

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

    /**
     * Valida que el usuario autenticado tenga permisos para crear el tipo de usuario solicitado
     * - ADMINISTRADOR puede crear PROPIETARIOS
     * - PROPIETARIO puede crear EMPLEADOS
     */
    private void validateRoleCreationPermissions(RoleModel roleToCreate) {
        if (roleToCreate == null || roleToCreate.getId() == null) {
            throw new IllegalArgumentException("El rol del usuario a crear es requerido");
        }

        String currentUserRole = securityContextPort.getCurrentUserRole();
        Long roleIdToCreate = roleToCreate.getId();

        // Si intenta crear un PROPIETARIO, debe ser ADMINISTRADOR
        if (roleIdToCreate.equals(RoleEnum.PROPIETARIO.getRoleId())) {
            if (!RoleEnum.ADMINISTRADOR.getName().equalsIgnoreCase(currentUserRole)) {
                throw new UnauthorizedRoleCreationException(
                    "Solo los usuarios con rol ADMINISTRADOR pueden crear usuarios PROPIETARIO"
                );
            }
        }

        // Si intenta crear un EMPLEADO, debe ser PROPIETARIO
        if (roleIdToCreate.equals(RoleEnum.EMPLEADO.getRoleId())) {
            if (!RoleEnum.PROPIETARIO.getName().equalsIgnoreCase(currentUserRole)) {
                throw new UnauthorizedRoleCreationException(
                    "Solo los usuarios con rol PROPIETARIO pueden crear usuarios EMPLEADO"
                );
            }
        }

        // No se permite crear ADMINISTRADORES ni CLIENTES desde este endpoint
        if (roleIdToCreate.equals(RoleEnum.ADMINISTRADOR.getRoleId())) {
            throw new UnauthorizedRoleCreationException(
                "No se permite crear usuarios con rol ADMINISTRADOR desde este endpoint"
            );
        }

        if (roleIdToCreate.equals(RoleEnum.CLIENTE.getRoleId())) {
            throw new UnauthorizedRoleCreationException(
                "No se permite crear usuarios con rol CLIENTE desde este endpoint"
            );
        }
    }
}
