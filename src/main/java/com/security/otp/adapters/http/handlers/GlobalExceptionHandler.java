package com.security.otp.adapters.http.handlers;

import com.security.otp.core.exceptions.AppException;
import com.security.otp.core.exceptions.conflict.ConflictException;
import com.security.otp.core.exceptions.expired.ExpiredOtpException;
import com.security.otp.core.exceptions.internal.InternalException;
import com.security.otp.core.exceptions.not_found.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {}

    @ExceptionHandler({NotFoundException.class, ConflictException.class, ExpiredOtpException.class})
    public ResponseEntity<Map<String, Object>> handleAppException(final AppException ex) {
        final HttpStatus status = switch (ex) {
            case NotFoundException _ -> HttpStatus.NOT_FOUND;
            case ConflictException _ -> HttpStatus.CONFLICT;
            case ExpiredOtpException _ -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };

        return this.buildResponse(
                status,
                ex.getCode(),
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return this.buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Validation failed",
                errors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(final ConstraintViolationException ex) {
        return this.buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler({InternalException.class, Exception.class})
    public ResponseEntity<Map<String, Object>> handleInternalException(final Exception ex) {

        final String message = ex.getMessage() != null
                && !(ex instanceof InternalException)
                    ? InternalException.MESSAGE
                    : Objects.requireNonNullElse(ex.getMessage(), InternalException.MESSAGE);

        return this.buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                InternalException.CODE,
                message,
                null
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            final HttpStatus status,
            final String code,
            final String message,
            final Object errors
    ) {
        final Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", code);
        body.put("message", message);

        if (errors != null) {
            body.put("errors", errors);
        }

        return ResponseEntity.status(status).body(body);
    }

}