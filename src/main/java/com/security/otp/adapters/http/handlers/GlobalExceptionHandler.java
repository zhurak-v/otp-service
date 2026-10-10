package com.security.otp.adapters.http.handlers;

import com.security.otp.core.exceptions.AppException;
import com.security.otp.core.exceptions.conflict.ConflictException;
import com.security.otp.core.exceptions.expired.ExpiredOtpException;
import com.security.otp.core.exceptions.internal.InternalException;
import com.security.otp.core.exceptions.not_found.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {}

    @ExceptionHandler({NotFoundException.class, ConflictException.class, ExpiredOtpException.class})
    public ResponseEntity<Map<String, Object>> handleAppException(final AppException ex) {
        final HttpStatus status = switch (ex) {
            case NotFoundException _ -> HttpStatus.NOT_FOUND;
            case ConflictException _ -> HttpStatus.CONFLICT;
            case ExpiredOtpException _ -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };

        return this.buildResponse(status, ex.getCode(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new LinkedHashMap<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.merge(error.getField(), String.valueOf(error.getDefaultMessage()), (a, b) -> a + "; " + b);
        }

        return this.buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(final ConstraintViolationException ex) {
        return this.buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedRequest(final HttpMessageNotReadableException ex) {
        return this.buildResponse(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Malformed request body", null);
    }

    @ExceptionHandler({InternalException.class, Exception.class})
    public ResponseEntity<Map<String, Object>> handleInternalException(final Exception ex) {
        LOGGER.error("Unhandled exception", ex);

        final String message = ex instanceof InternalException && ex.getMessage() != null
                ? ex.getMessage()
                : InternalException.MESSAGE;

        return this.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, InternalException.CODE, message, null);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            final HttpStatus status,
            final String code,
            final String message,
            final Object errors
    ) {
        final Map<String, Object> body = new LinkedHashMap<>();
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
