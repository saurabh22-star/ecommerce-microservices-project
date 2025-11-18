package com.ecommerce.ecommercehub.productmodule.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.ecommercehub.productmodule.controllers.CartController;
import com.ecommerce.ecommercehub.productmodule.dtos.APIResponseDTO;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        APIResponseDTO response = new APIResponseDTO(ex.getMessage(), false);
        logger.error("Resource not found: ", ex);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponseDTO> handleAPIException(APIException ex) {
        APIResponseDTO response = new APIResponseDTO(ex.getMessage(), false);
        logger.error("API exception: ", ex);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        logger.error("Validation failed: ", ex);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolations(ConstraintViolationException ex) {
        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String property = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            violations.put(property, message);
        });
        logger.error("Constraint violation: ", ex);
        return new ResponseEntity<>(violations, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<APIResponseDTO> handleMissingPathVariable(MissingPathVariableException ex) {
        APIResponseDTO response = new APIResponseDTO(ex.getMessage(), false);
        logger.error("Missing path variable: ", ex);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<APIResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        APIResponseDTO response = new APIResponseDTO(ex.getMessage(), false);
        logger.error("Data integrity violation: ", ex);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}