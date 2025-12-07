package com.pragma.powerup.domain.usecase;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UserUseCase
 * Cubre las siguientes Historias de Usuario:
 * - HU1: Crear Propietario
 * - HU6: Crear cuenta empleado
 * - HU8: Crear cuenta Cliente
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserUseCase - Gestión de Usuarios")
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private IRolePersistencePort rolePersistencePort;

    @Mock
    private ISecurityContextPort securityContextPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private RoleModel adminRole;
    private RoleModel ownerRole;
    private RoleModel employeeRole;
    private RoleModel clientRole;

    @BeforeEach
    void setUp() {
        adminRole = new RoleModel(RoleEnum.ADMINISTRADOR.getRoleId(), RoleEnum.ADMINISTRADOR.getName(), "Admin");
        ownerRole = new RoleModel(RoleEnum.PROPIETARIO.getRoleId(), RoleEnum.PROPIETARIO.getName(), "Owner");
        employeeRole = new RoleModel(RoleEnum.EMPLEADO.getRoleId(), RoleEnum.EMPLEADO.getName(), "Employee");
        clientRole = new RoleModel(RoleEnum.CLIENTE.getRoleId(), RoleEnum.CLIENTE.getName(), "Client");
    }

    @Nested
    @DisplayName("HU1: Crear Propietario")
    class CreateOwnerTests {

        @Test
        @DisplayName("Happy Path: Debe crear propietario mayor de edad correctamente")
        void shouldCreateOwnerWhenUserIsAdultAndAdminIsAuthenticated() {
            // Arrange
            UserModel userModel = createUserModel(ownerRole, LocalDate.now().minusYears(25));

            when(rolePersistencePort.findById(ownerRole.getId())).thenReturn(Optional.of(ownerRole));
            when(securityContextPort.getCurrentUserRole()).thenReturn(RoleEnum.ADMINISTRADOR.getName());
            when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.saveUser(any(UserModel.class))).thenReturn(userModel);

            // Act
            UserModel result = userUseCase.createUser(userModel);

            // Assert
            assertNotNull(result);
            verify(rolePersistencePort).findById(ownerRole.getId());
            verify(passwordEncoderPort).encode(anyString());
            verify(userPersistencePort).saveUser(any(UserModel.class));
        }

        @Test
        @DisplayName("Validación: Debe rechazar propietario menor de edad")
        void shouldThrowExceptionWhenOwnerIsUnderage() {
            // Arrange
            UserModel userModel = createUserModel(ownerRole, LocalDate.now().minusYears(17));

            when(rolePersistencePort.findById(ownerRole.getId())).thenReturn(Optional.of(ownerRole));
            when(securityContextPort.getCurrentUserRole()).thenReturn(RoleEnum.ADMINISTRADOR.getName());

            // Act & Assert
            assertThrows(UserUnderageException.class, () -> userUseCase.createUser(userModel));
            verify(userPersistencePort, never()).saveUser(any(UserModel.class));
        }

        @Test
        @DisplayName("Error: Debe rechazar creación de propietario sin rol administrador")
        void shouldThrowExceptionWhenNonAdminTriesToCreateOwner() {
            // Arrange
            UserModel userModel = createUserModel(ownerRole, LocalDate.now().minusYears(25));

            when(rolePersistencePort.findById(ownerRole.getId())).thenReturn(Optional.of(ownerRole));
            when(securityContextPort.getCurrentUserRole()).thenReturn(RoleEnum.PROPIETARIO.getName());

            // Act & Assert
            assertThrows(UnauthorizedRoleCreationException.class, () -> userUseCase.createUser(userModel));
            verify(userPersistencePort, never()).saveUser(any(UserModel.class));
        }
    }

    @Nested
    @DisplayName("HU6: Crear cuenta empleado")
    class CreateEmployeeTests {

        @Test
        @DisplayName("Happy Path: Debe crear empleado cuando propietario está autenticado")
        void shouldCreateEmployeeWhenOwnerIsAuthenticated() {
            // Arrange
            UserModel userModel = createUserModel(employeeRole, LocalDate.now().minusYears(20));

            when(rolePersistencePort.findById(employeeRole.getId())).thenReturn(Optional.of(employeeRole));
            when(securityContextPort.getCurrentUserRole()).thenReturn(RoleEnum.PROPIETARIO.getName());
            when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.saveUser(any(UserModel.class))).thenReturn(userModel);

            // Act
            UserModel result = userUseCase.createUser(userModel);

            // Assert
            assertNotNull(result);
            assertEquals(employeeRole, result.getRole());
            verify(passwordEncoderPort).encode(anyString());
            verify(userPersistencePort).saveUser(any(UserModel.class));
        }

        @Test
        @DisplayName("Validación: Debe rechazar creación de empleado por usuario no propietario")
        void shouldThrowExceptionWhenNonOwnerTriesToCreateEmployee() {
            // Arrange
            UserModel userModel = createUserModel(employeeRole, LocalDate.now().minusYears(20));

            when(rolePersistencePort.findById(employeeRole.getId())).thenReturn(Optional.of(employeeRole));
            when(securityContextPort.getCurrentUserRole()).thenReturn(RoleEnum.ADMINISTRADOR.getName());

            // Act & Assert
            assertThrows(UnauthorizedRoleCreationException.class, () -> userUseCase.createUser(userModel));
            verify(userPersistencePort, never()).saveUser(any(UserModel.class));
        }

        @Test
        @DisplayName("Error: Debe fallar cuando el rol no existe en el sistema")
        void shouldThrowExceptionWhenRoleNotFound() {
            // Arrange
            UserModel userModel = createUserModel(employeeRole, LocalDate.now().minusYears(20));

            when(rolePersistencePort.findById(employeeRole.getId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NoDataFoundException.class, () -> userUseCase.createUser(userModel));
            verify(userPersistencePort, never()).saveUser(any(UserModel.class));
        }
    }

    @Nested
    @DisplayName("HU8: Crear cuenta Cliente")
    class CreateClientTests {

        @Test
        @DisplayName("Happy Path: Debe crear cliente sin autenticación")
        void shouldCreateClientWithoutAuthentication() {
            // Arrange
            UserModel userModel = createUserModel(clientRole, LocalDate.now().minusYears(20));

            when(rolePersistencePort.findById(clientRole.getId())).thenReturn(Optional.of(clientRole));
            when(securityContextPort.getCurrentUserRole()).thenThrow(new RuntimeException("No authenticated"));
            when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.saveUser(any(UserModel.class))).thenReturn(userModel);

            // Act
            UserModel result = userUseCase.createUser(userModel);

            // Assert
            assertNotNull(result);
            assertEquals(clientRole, result.getRole());
            verify(passwordEncoderPort).encode(anyString());
            verify(userPersistencePort).saveUser(any(UserModel.class));
        }

        @Test
        @DisplayName("Validación: Debe crear cliente con cualquier edad válida")
        void shouldCreateClientWithAnyAge() {
            // Arrange - Cliente menor de 18 años (no se valida edad para clientes)
            UserModel userModel = createUserModel(clientRole, LocalDate.now().minusYears(16));

            when(rolePersistencePort.findById(clientRole.getId())).thenReturn(Optional.of(clientRole));
            when(securityContextPort.getCurrentUserRole()).thenThrow(new RuntimeException("No authenticated"));
            when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.saveUser(any(UserModel.class))).thenReturn(userModel);

            // Act
            UserModel result = userUseCase.createUser(userModel);

            // Assert
            assertNotNull(result);
            verify(userPersistencePort).saveUser(any(UserModel.class));
        }

        @Test
        @DisplayName("Error: Usuario no autenticado no puede crear otros roles")
        void shouldThrowExceptionWhenUnauthenticatedUserTriesToCreateNonClientRole() {
            // Arrange
            UserModel userModel = createUserModel(ownerRole, LocalDate.now().minusYears(25));

            when(rolePersistencePort.findById(ownerRole.getId())).thenReturn(Optional.of(ownerRole));
            when(securityContextPort.getCurrentUserRole()).thenThrow(new RuntimeException("No authenticated"));

            // Act & Assert
            assertThrows(UnauthorizedRoleCreationException.class, () -> userUseCase.createUser(userModel));
            verify(userPersistencePort, never()).saveUser(any(UserModel.class));
        }
    }

    @Nested
    @DisplayName("Edge Cases y Validaciones Adicionales")
    class EdgeCasesTests {

        @Test
        @DisplayName("Validación: Debe rechazar creación de usuario administrador")
        void shouldThrowExceptionWhenTryingToCreateAdmin() {
            // Arrange
            UserModel userModel = createUserModel(adminRole, LocalDate.now().minusYears(30));

            when(rolePersistencePort.findById(adminRole.getId())).thenReturn(Optional.of(adminRole));
            when(securityContextPort.getCurrentUserRole()).thenReturn(RoleEnum.ADMINISTRADOR.getName());

            // Act & Assert
            assertThrows(UnauthorizedRoleCreationException.class, () -> userUseCase.createUser(userModel));
            verify(userPersistencePort, never()).saveUser(any(UserModel.class));
        }

        @Test
        @DisplayName("Validación: Debe encriptar la contraseña antes de guardar")
        void shouldEncryptPasswordBeforeSaving() {
            // Arrange
            String plainPassword = "plainPassword123";
            String encodedPassword = "encodedPassword456";
            UserModel userModel = createUserModel(clientRole, LocalDate.now().minusYears(20));
            userModel.setPassword(plainPassword);

            when(rolePersistencePort.findById(clientRole.getId())).thenReturn(Optional.of(clientRole));
            when(securityContextPort.getCurrentUserRole()).thenThrow(new RuntimeException("No authenticated"));
            when(passwordEncoderPort.encode(plainPassword)).thenReturn(encodedPassword);
            when(userPersistencePort.saveUser(any(UserModel.class))).thenReturn(userModel);

            // Act
            userUseCase.createUser(userModel);

            // Assert
            verify(passwordEncoderPort).encode(plainPassword);
            assertEquals(encodedPassword, userModel.getPassword());
        }

        @Test
        @DisplayName("Edge Case: Propietario exactamente de 18 años debe ser aceptado")
        void shouldAcceptOwnerExactly18YearsOld() {
            // Arrange - Exactamente 18 años
            UserModel userModel = createUserModel(ownerRole, LocalDate.now().minusYears(18));

            when(rolePersistencePort.findById(ownerRole.getId())).thenReturn(Optional.of(ownerRole));
            when(securityContextPort.getCurrentUserRole()).thenReturn(RoleEnum.ADMINISTRADOR.getName());
            when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPassword");
            when(userPersistencePort.saveUser(any(UserModel.class))).thenReturn(userModel);

            // Act
            UserModel result = userUseCase.createUser(userModel);

            // Assert
            assertNotNull(result);
            verify(userPersistencePort).saveUser(any(UserModel.class));
        }

        @Test
        @DisplayName("Validación: Debe fallar si el rol del usuario es null")
        void shouldThrowExceptionWhenRoleIsNull() {
            // Arrange
            UserModel userModel = createUserModel(null, LocalDate.now().minusYears(20));

            // Act & Assert
            assertThrows(NullPointerException.class, () -> userUseCase.createUser(userModel));
        }
    }

    // Método auxiliar para crear UserModel
    private UserModel createUserModel(RoleModel role, LocalDate birthDate) {
        UserModel user = new UserModel();
        user.setName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setIdentificationDocument("123456789");
        user.setPhoneNumber("+573001234567");
        user.setBirthDate(birthDate);
        user.setRole(role);
        return user;
    }
}

