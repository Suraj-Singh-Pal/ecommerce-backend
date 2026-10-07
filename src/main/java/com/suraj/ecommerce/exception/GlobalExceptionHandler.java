package com.suraj.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.validation.ConstraintViolationException;
import com.suraj.ecommerce.exception.PriceChangedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException exception) {

    Map<String, String> errors = new LinkedHashMap<>();

    exception.getConstraintViolations().forEach(violation ->
            errors.put(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            )
    );

    ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            errors.toString(),
            LocalDateTime.now()
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
}

    // Handle Invalid JSON Request Body
@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<ErrorResponse> handleInvalidRequestBody(
        HttpMessageNotReadableException exception) {

    ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid request body. Please check your JSON format.",
            LocalDateTime.now()
    );

    return new ResponseEntity<>(
            errorResponse,
            HttpStatus.BAD_REQUEST
    );
}

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Required parameter is missing: " + exception.getParameterName(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid parameter value: " + exception.getName(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


@ExceptionHandler(PriceChangedException.class)
public ResponseEntity<Map<String, Object>> handlePriceChanged(
        PriceChangedException exception) {

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("status", HttpStatus.CONFLICT.value());
    response.put("message", exception.getMessage());
    response.put("priceChanges", exception.getPriceChanges());
    response.put("timestamp", LocalDateTime.now());

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(response);
}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred.",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @ExceptionHandler(NoResourceFoundException.class)
public ResponseEntity<ErrorResponse> handleNoResourceFound(
        NoResourceFoundException exception) {

    ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Endpoint not found: " + exception.getResourcePath(),
            LocalDateTime.now()
    );

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorResponse);
}

@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException exception) {

    ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "You do not have permission to access this resource.",
            LocalDateTime.now()
    );

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(errorResponse);
}

}