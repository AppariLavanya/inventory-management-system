package com.example.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================================
    //  INVALID INPUT / BAD REQUEST
    // ================================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ================================
    //  ENTITY NOT FOUND (Product/Order/User)
    // ================================
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ================================
    //  INVALID ORDER STATUS
    // ================================
    @ExceptionHandler(OrderStatusException.class)
    public ResponseEntity<?> handleOrderStatus(OrderStatusException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ================================
    //  AUTHENTICATION FAILURE
    // ================================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    // ================================
    //  ROLE / PERMISSION FAILURE
    // ================================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "You are not allowed to perform this action");
    }

    // ================================
    //  REQUEST BODY VALIDATION ERRORS
    // ================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    // ================================
    //  EXPORT ERRORS (CSV, Excel, PDF)
    // ================================
    @ExceptionHandler(ExportException.class)
    public ResponseEntity<?> handleExportException(ExportException ex, HttpServletRequest request) {
        // If an export request somehow reached this handler, return a simple error JSON.
        // (Export endpoints in controller catch and write errors directly, so normally this won't be hit.)
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Export failed: " + ex.getMessage());
    }

    // ================================
    //  ANALYTICS ERRORS
    // ================================
    @ExceptionHandler(AnalyticsException.class)
    public ResponseEntity<?> handleAnalyticsException(AnalyticsException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Analytics error: " + ex.getMessage());
    }

    // ================================
    //  RUNTIME ERRORS
    // ================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ================================
    //  CATCH-ALL FALLBACK
    // ================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again.");
    }

    // ================================
    //  Standardized API error response
    // ================================
    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }
}





