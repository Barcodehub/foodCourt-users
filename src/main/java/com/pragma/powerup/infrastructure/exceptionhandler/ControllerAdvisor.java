package com.pragma.powerup.infrastructure.exceptionhandler;

import com.pragma.powerup.domain.exception.UserUnderageException;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor {

    private static final String MESSAGE = "message";
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoDataFoundException(
            NoDataFoundException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.NOT_FOUND.value());
        response.put(ERROR, HttpStatus.NOT_FOUND.getReasonPhrase());
        response.put(MESSAGE, ExceptionResponse.NO_DATA_FOUND.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserUnderageException.class)
    public ResponseEntity<Map<String, Object>> handleUserUnderageException(
            UserUnderageException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR, "User Underage");
        response.put(MESSAGE, exception.getMessage()); // Usa el mensaje personalizado con la edad

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.CONFLICT.value());
        response.put(ERROR, "Data Integrity Violation");

        // Detectar tipo de duplicado basado en el mensaje de la excepción
        String message = ExceptionResponse.DUPLICATE_USER_DATA.getMessage();
        String exceptionMessage = exception.getMessage();

        if (exceptionMessage != null) {
            if (exceptionMessage.contains("email")) {
                message = ExceptionResponse.EMAIL_ALREADY_EXISTS.getMessage();
            } else if (exceptionMessage.contains("identification_document")) {
                message = ExceptionResponse.IDENTIFICATION_ALREADY_EXISTS.getMessage();
            }
        }

        response.put(MESSAGE, message);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR, "Validation Failed");
        response.put(MESSAGE, ExceptionResponse.VALIDATION_FAILED.getMessage());
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception exception) {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.put(MESSAGE, ExceptionResponse.INTERNAL_SERVER_ERROR.getMessage());

        // Log the actual exception for debugging (no exponer al cliente)
        exception.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
}


