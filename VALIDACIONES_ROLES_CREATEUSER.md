# Validaciones de Roles en createUser

## Resumen de Implementación

Se han implementado las siguientes validaciones en el servicio `createUser`:

### 1. Validación para crear Usuario PROPIETARIO
- **Requisito**: Solo usuarios con rol **ADMINISTRADOR** pueden crear usuarios PROPIETARIO
- **Comportamiento**: Si un usuario que NO es ADMINISTRADOR intenta crear un PROPIETARIO, se lanza una excepción `UnauthorizedRoleCreationException`

### 2. Validación para crear Usuario EMPLEADO
- **Requisito**: Solo usuarios con rol **PROPIETARIO** pueden crear usuarios EMPLEADO
- **Comportamiento**: Si un usuario que NO es PROPIETARIO intenta crear un EMPLEADO, se lanza una excepción `UnauthorizedRoleCreationException`

### 3. Restricciones adicionales
- **No se permite** crear usuarios con rol ADMINISTRADOR desde este endpoint
- **No se permite** crear usuarios con rol CLIENTE desde este endpoint

## Archivos Modificados

### 1. **open-api.yaml**
- Se agregó el campo `roleId` (obligatorio) al schema `UserRequest`
- Este campo especifica el rol que se asignará al usuario a crear

### 2. **ISecurityContextPort.java** (NUEVO)
- Puerto SPI para acceder al contexto de seguridad desde la capa de dominio
- Métodos:
  - `getCurrentUserId()`: Obtiene el ID del usuario autenticado
  - `getCurrentUserRole()`: Obtiene el rol del usuario autenticado
  - `hasRole(String roleName)`: Verifica si el usuario tiene un rol específico

### 3. **SecurityContextAdapter.java** (NUEVO)
- Adaptador que implementa `ISecurityContextPort`
- Utiliza `SecurityContextUtil` para obtener información del usuario autenticado

### 4. **UnauthorizedRoleCreationException.java** (NUEVO)
- Excepción personalizada para errores de autorización en creación de usuarios
- Se lanza cuando un usuario intenta crear otro usuario sin los permisos adecuados

### 5. **UserUseCase.java**
- Se inyectó `ISecurityContextPort` como dependencia
- Se modificó el método `createUser()`:
  - Ya no asigna automáticamente el rol PROPIETARIO
  - Ahora usa el `roleId` enviado en la petición
  - Llama a `validateRoleCreationPermissions()` antes de crear el usuario
- Se agregó el método `validateRoleCreationPermissions()`:
  - Valida que el usuario autenticado tenga permisos para crear el tipo de usuario solicitado
  - Implementa la lógica de negocio para ADMINISTRADOR → PROPIETARIO y PROPIETARIO → EMPLEADO

### 6. **BeanConfiguration.java**
- Se actualizó el bean `userServicePort` para inyectar `ISecurityContextPort`

### 7. **IUserMapper.java**
- Se agregó mapeo de `roleId` a `RoleModel` con el método `roleIdToRoleModel()`
- Utiliza `@Mapping` con `qualifiedByName` para convertir el Long roleId en un objeto RoleModel

### 8. **UserController.java**
- Se mantiene la anotación `@RequireRole({RoleEnum.ADMINISTRADOR, RoleEnum.PROPIETARIO})`
- Ambos roles pueden acceder al endpoint, pero la validación específica se hace en el caso de uso

### 9. **ControllerAdvisor.java**
- Se agregó handler para `UnauthorizedRoleCreationException`
- Retorna HTTP 403 FORBIDDEN con el mensaje descriptivo de la excepción

## Flujo de Validación

1. El usuario autenticado (ADMINISTRADOR o PROPIETARIO) envía una petición POST a `/users`
2. El DTO incluye `roleId` indicando qué tipo de usuario desea crear
3. El `UserController` valida que el usuario tenga rol ADMINISTRADOR o PROPIETARIO (nivel de acceso básico)
4. El `UserHandler` delega al caso de uso
5. El `UserUseCase`:
   - Valida la edad del usuario a crear
   - **Valida los permisos** según la tabla:
     - roleId = 2 (PROPIETARIO) → requiere currentUserRole = ADMINISTRADOR
     - roleId = 3 (EMPLEADO) → requiere currentUserRole = PROPIETARIO
     - roleId = 1 (ADMINISTRADOR) → rechazado
     - roleId = 4 (CLIENTE) → rechazado
   - Si la validación falla, lanza `UnauthorizedRoleCreationException` (HTTP 403)
   - Si pasa, codifica la contraseña y guarda el usuario

## Ejemplos de Uso

### Crear un PROPIETARIO (requiere ser ADMINISTRADOR)
```json
POST /users
Authorization: Bearer {token_de_administrador}
Content-Type: application/json

{
  "name": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@email.com",
  "password": "MiClaveSegura123",
  "identificationDocument": "1234567890",
  "phoneNumber": "+573005698325",
  "birthDate": "1990-01-01",
  "roleId": 2
}
```
**Resultado**: ✅ Usuario PROPIETARIO creado exitosamente

---

### Crear un EMPLEADO (requiere ser PROPIETARIO)
```json
POST /users
Authorization: Bearer {token_de_propietario}
Content-Type: application/json

{
  "name": "María",
  "lastName": "García",
  "email": "maria.garcia@email.com",
  "password": "ClaveSegura456",
  "identificationDocument": "9876543210",
  "phoneNumber": "+573001234567",
  "birthDate": "1995-05-15",
  "roleId": 3
}
```
**Resultado**: ✅ Usuario EMPLEADO creado exitosamente

---

### Error: PROPIETARIO intenta crear otro PROPIETARIO
```json
POST /users
Authorization: Bearer {token_de_propietario}
Content-Type: application/json

{
  "name": "Pedro",
  "lastName": "López",
  "email": "pedro.lopez@email.com",
  "password": "OtraClave789",
  "identificationDocument": "5555555555",
  "phoneNumber": "+573009876543",
  "birthDate": "1988-12-20",
  "roleId": 2
}
```
**Resultado**: ❌ HTTP 403 Forbidden
```json
{
  "timestamp": "2025-11-30T...",
  "status": 403,
  "error": "Unauthorized Role Creation",
  "message": "Solo los usuarios con rol ADMINISTRADOR pueden crear usuarios PROPIETARIO"
}
```

---

### Error: ADMINISTRADOR intenta crear un EMPLEADO
```json
POST /users
Authorization: Bearer {token_de_administrador}
Content-Type: application/json

{
  "name": "Ana",
  "lastName": "Martínez",
  "email": "ana.martinez@email.com",
  "password": "Password123",
  "identificationDocument": "7777777777",
  "phoneNumber": "+573007654321",
  "birthDate": "1992-03-10",
  "roleId": 3
}
```
**Resultado**: ❌ HTTP 403 Forbidden
```json
{
  "timestamp": "2025-11-30T...",
  "status": 403,
  "error": "Unauthorized Role Creation",
  "message": "Solo los usuarios con rol PROPIETARIO pueden crear usuarios EMPLEADO"
}
```

## Próximos Pasos

1. **Recompilar el proyecto** para generar los DTOs actualizados:
   ```powershell
   .\gradlew clean compileJava
   ```

2. **Probar los endpoints** con diferentes roles y escenarios

3. **Opcional**: Agregar tests unitarios para validar la lógica de permisos en `UserUseCase`

