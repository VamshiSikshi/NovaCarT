package com.shivamsikarwar.novacart.exception;

import com.shivamsikarwar.novacart.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setMessage("Validation failed");
        response.setPath(request.getRequestURI());
        response.setErrors(errors);

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request) {

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setError(HttpStatus.CONFLICT.getReasonPhrase());
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        response.setMessage("Access Denied");
        response.setPath(request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unexpected error while processing request: {}",
                request.getRequestURI(),
                ex
        );

        ApiErrorResponse response = new ApiErrorResponse();

        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.setMessage("An unexpected error occurred");
        response.setPath(request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}