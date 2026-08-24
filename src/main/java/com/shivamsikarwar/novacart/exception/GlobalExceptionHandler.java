package com.shivamsikarwar.novacart.exception;

import com.shivamsikarwar.novacart.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =========================
    // VALIDATION
    // =========================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiErrorResponse response = buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request
        );

        response.setErrors(errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // =========================
    // NOT FOUND
    // =========================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    // =========================
    // CONFLICT
    // =========================

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidOrderStatus(
            InvalidOrderStatusException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStock(
            InsufficientStockException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPayment(
            InvalidPaymentException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    // =========================
    // UNAUTHORIZED
    // =========================

    @ExceptionHandler(AuthenticatedUserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticatedUserNotFound(
            AuthenticatedUserNotFoundException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                request
        );
    }

    // =========================
    // FORBIDDEN
    // =========================

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                request
        );
    }

    // =========================
    // BAD REQUEST
    // =========================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                "Invalid request body",
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return buildResponseEntity(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    // =========================
    // GENERIC ERROR
    // =========================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unexpected error while processing request: {}",
                request.getRequestURI(),
                ex
        );

        return buildResponseEntity(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request
        );
    }

    // =========================
    // RESPONSE BUILDER
    // =========================

    private ResponseEntity<ApiErrorResponse> buildResponseEntity(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ApiErrorResponse response =
                buildResponse(
                        status,
                        message,
                        request
                );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private ApiErrorResponse buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ApiErrorResponse response =
                new ApiErrorResponse();

        response.setTimestamp(
                LocalDateTime.now()
        );

        response.setStatus(
                status.value()
        );

        response.setError(
                status.getReasonPhrase()
        );

        response.setMessage(message);

        response.setPath(
                request.getRequestURI()
        );

        return response;
    }
}