package com.api.cloudx.exception;

import com.api.cloudx.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. DTO Validation Errors (@Valid @NotBlank)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        return ApiResponse.<Object>builder()
                .message("Validation failed.")
                .success(false)
                .timestamp(LocalDateTime.now())
                .errors(fieldErrors)
                .build();
    }

    // 2. Custom Business Logic Errors (The "Pro" Way)
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiResponse<Object> handleBusinessException(BusinessException ex) {
        return ApiResponse.<Object>builder()
                .message(ex.getMessage())
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 3. Missing or Malformed Body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleMissingBody(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("body", "Request body is required and must be valid JSON");
        return ApiResponse.<Object>builder()
                .message("The request body is missing or malformed.")
                .success(false)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    // 4. Duplicate Database Entry / Constraints
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Object> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ApiResponse.<Object>builder()
                .message("Database conflict: This record already exists or violates a constraint.")
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 5. Wrong Data Type in URL (e.g., /123 vs /abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown";
        String message = String.format("The parameter '%s' should be of type '%s'", ex.getName(), type);
        return ApiResponse.<Object>builder()
                .message(message)
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 6. Wrong HTTP Method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ApiResponse.<Object>builder()
                .message("Method '" + ex.getMethod() + "' not allowed. Use: " + ex.getSupportedHttpMethods())
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 7. General Runtime (Not Found, etc.)
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> handleRuntimeException(RuntimeException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // 8. Catch-all Final (Internal Server Error)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleGeneralException(Exception ex) {
        return ApiResponse.error("Internal Server Error: " + ex.getMessage());
    }
}