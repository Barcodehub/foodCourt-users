package com.pragma.powerup.domain.exception;

/**
 * Excepción lanzada cuando un usuario intenta crear otro usuario sin los permisos adecuados
 */
public class UnauthorizedRoleCreationException extends RuntimeException {
    public UnauthorizedRoleCreationException(String message) {
        super(message);
    }
}

