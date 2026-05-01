package com.sport.managementsport.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ErrorResponseDTO buildErrorResponse(String message, HttpStatus status, String path, List<ValidationErrorDTO> validationErrors) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(status.value(), status.getReasonPhrase(), message, path);
        errorResponse.setTraceId(UUID.randomUUID().toString());
        errorResponse.setValidationErrors(validationErrors);
        return errorResponse;
    }

    // === MANEJADORES DE EXCEPCIONES DE SEGURIDAD ===

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("AccessDeniedException: {} en {}", ex.getMessage(), request.getRequestURI());
        ErrorResponseDTO errorResponse = buildErrorResponse("Acceso denegado. No tienes los permisos necesarios para realizar esta acción.", HttpStatus.FORBIDDEN, request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.warn("AuthenticationException: {} en {}", ex.getMessage(), request.getRequestURI());
        ErrorResponseDTO errorResponse = buildErrorResponse("Credenciales inválidas. Por favor, verifica tu usuario y contraseña.", HttpStatus.UNAUTHORIZED, request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // === MANEJADORES DE EXCEPCIONES DE NEGOCIO ===

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("ResourceNotFoundException: {} en {}", ex.getMessage(), request.getRequestURI());
        ErrorResponseDTO errorResponse = buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("DuplicateResourceException: {} en {}", ex.getMessage(), request.getRequestURI());
        ErrorResponseDTO errorResponse = buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {
        log.warn("BusinessRuleException: {} en {}", ex.getMessage(), request.getRequestURI());
        ErrorResponseDTO errorResponse = buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceInactiveException.class)
    public ResponseEntity<ErrorResponseDTO> handleInactiveResource(ResourceInactiveException ex, HttpServletRequest request) {
        log.warn("ResourceInactiveException: {} en {}", ex.getMessage(), request.getRequestURI());
        ErrorResponseDTO error = buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), null);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // === MANEJADOR DE VALIDACIONES ===

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationErrorDTO> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorDTO(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponseDTO error = buildErrorResponse("Error de validación. Por favor, revise los campos.", HttpStatus.BAD_REQUEST, request.getRequestURI(), validationErrors);
        log.warn("MethodArgumentNotValidException en {}: {}", request.getRequestURI(), validationErrors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // === MANEJADOR GENERAL ===

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Error no esperado en {}", request.getRequestURI(), ex);
        ErrorResponseDTO error = buildErrorResponse("Ocurrió un error inesperado en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), null);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
