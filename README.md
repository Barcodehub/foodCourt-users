<br />
<div align="center">
<h3 align="center">PRAGMA POWER-UP - USERS MICROSERVICE</h3>
  <p align="center">
    Microservicio de gestión de usuarios y autenticación para el sistema de restaurantes. Maneja la creación de usuarios con diferentes roles, autenticación JWT y validaciones de permisos.
  </p>
</div>

### Built With

* ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
* ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
* ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white)
* ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
* ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
* ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)

## Descripción

Este microservicio es responsable de:
- **Gestión de usuarios**: Creación, consulta y listado de usuarios
- **Autenticación**: Login con JWT (JSON Web Tokens)
- **Autorización**: Validación de roles y permisos
- **Roles soportados**:
  - `ADMINISTRADOR`: Puede crear usuarios PROPIETARIO
  - `PROPIETARIO`: Puede crear usuarios EMPLEADO
  - `EMPLEADO`: Personal de restaurantes
  - `CLIENTE`: Usuarios finales

### Arquitectura

El proyecto sigue **Arquitectura Hexagonal (Puertos y Adaptadores)** con **API-First Design**:

```
src/
├── domain/              # Lógica de negocio pura
│   ├── model/          # Modelos de dominio
│   ├── usecase/        # Casos de uso
│   ├── api/            # Puertos de entrada (interfaces)
│   └── spi/            # Puertos de salida (interfaces)
├── application/         # Capa de aplicación
│   ├── handler/        # Handlers/Facades
│   └── mapper/         # Mappers (MapStruct)
└── infrastructure/      # Adaptadores
    ├── input/rest/     # Controladores REST
    ├── out/jpa/        # Adaptadores de persistencia
    ├── out/security/   # Adaptadores de seguridad
    └── security/       # Configuración de seguridad
```


<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these steps.

### Prerequisites

* JDK 17 [https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
* Gradle [https://gradle.org/install/](https://gradle.org/install/)
* PostgreSQL 14+ [https://www.postgresql.org/download/](https://www.postgresql.org/download/)

### Recommended Tools
* IntelliJ IDEA [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)
* Postman [https://www.postman.com/downloads/](https://www.postman.com/downloads/)
* DBeaver (para gestión de PostgreSQL) [https://dbeaver.io/download/](https://dbeaver.io/download/)

### Installation

1. Clone the repo
   ```sh
   git clone <repository-url>
   cd users
   ```

2. Create a new database in PostgreSQL
   ```sql
   CREATE DATABASE powerup_users;
   ```

3. Update the database connection settings
   ```yml
   # src/main/resources/application-dev.yml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/powerup_users
       username: postgres
       password: your_password
   ```

4. Configure environment variables (opcional)
   ```sh
   # Variables de entorno para JWT
   JWT_SECRET=your_secret_key_here
   JWT_EXPIRATION=86400000
   ```

<!-- USAGE -->
## Usage

### Compilar y generar código

Genera los DTOs e interfaces desde OpenAPI:
```sh
./gradlew openApiGenerate
```

Compila todo el proyecto:
```sh
./gradlew clean build
```

### Ejecutar la aplicación

Desde terminal:
```sh
./gradlew bootRun
```

O desde IntelliJ: Right-click PowerUpApplication → Run

La aplicación estará disponible en:
- API: [http://localhost:8081](http://localhost:8081)
- Swagger UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- OpenAPI spec: [http://localhost:8081/api-docs](http://localhost:8081/api-docs)

### Endpoints Principales

#### Autenticación
- `POST /auth/login` - Iniciar sesión (retorna JWT)

#### Usuarios
- `POST /users` - Crear usuario (requiere rol específico)
- `GET /users` - Listar usuarios (paginado)
- `GET /users/{id}` - Obtener usuario por ID

### Reglas de Negocio

#### Creación de Usuarios
- **ADMINISTRADOR** → puede crear **PROPIETARIO**
- **PROPIETARIO** → puede crear **EMPLEADO**
- No se permite crear ADMINISTRADOR ni CLIENTE desde el endpoint `/users`

#### Validaciones
- Los usuarios deben ser mayores de 18 años
- Email único en el sistema
- Documento de identidad único
- Contraseña mínima de 8 caracteres

### Desarrollo API-First

1. **Edita** el schema en `src/main/resources/static/open-api.yaml`
2. **Genera** los DTOs: `./gradlew openApiGenerate`
3. **Implementa** las interfaces generadas en los controladores
4. **Mapea** entre DTOs y modelos de dominio con MapStruct

Ejemplo de flujo:
```
open-api.yaml → openApiGenerate → DTOs generados → 
Controller (implementa interfaz) → Handler → UseCase → Domain
```

<!-- ROADMAP -->
## Tests

Run tests with coverage:
```sh
./gradlew test jacocoTestReport
```

O desde IntelliJ: Right-click test folder → Run tests with coverage

### Seguridad

#### JWT (JSON Web Tokens)
El microservicio usa JWT para autenticación y autorización:

**Claims en el token:**
- `userId`: ID del usuario autenticado
- `role`: Rol del usuario
- `roleId`: ID numérico del rol
- `email`: Email del usuario

**Flujo de autenticación:**
1. Usuario hace login con email/password
2. Sistema valida credenciales
3. Sistema genera token JWT
4. Cliente incluye token en header: `Authorization: Bearer <token>`
5. Sistema valida token en cada request

#### Roles y Permisos

| Rol | Puede crear | Endpoints permitidos |
|-----|------------|---------------------|
| ADMINISTRADOR | PROPIETARIO | POST /users (roleId=2) |
| PROPIETARIO | EMPLEADO | POST /users (roleId=3) |
| EMPLEADO | - | - |
| CLIENTE | - | - |

### Arquitectura Hexagonal

**Capas:**
- **Domain**: Lógica de negocio pura, sin dependencias externas
- **Application**: Orquestación y transformación de datos
- **Infrastructure**: Adaptadores (REST, JPA, Security)

**Puertos:**
- **API (Entrada)**: `IUserServicePort`, `IAuthenticationServicePort`
- **SPI (Salida)**: `IUserPersistencePort`, `IPasswordEncoderPort`, `ISecurityContextPort`

**Beneficios:**
- ✅ Testeable (dominio independiente)
- ✅ Mantenible (separación de responsabilidades)
- ✅ Flexible (fácil cambio de BD o framework)

### Documentación Adicional

- [VALIDACIONES_ROLES_CREATEUSER.md](VALIDACIONES_ROLES_CREATEUSER.md) - Detalles de validaciones de roles
- [IMPLEMENTACION_COMPLETADA.md](IMPLEMENTACION_COMPLETADA.md) - Guía de implementación completa


