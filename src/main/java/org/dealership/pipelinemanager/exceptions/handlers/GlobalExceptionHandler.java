package org.dealership.pipelinemanager.exceptions.handlers;

import org.dealership.pipelinemanager.exceptions.CycleDetectedException;
import org.dealership.pipelinemanager.exceptions.NodeNotFoundException;
import org.dealership.pipelinemanager.exceptions.PipelineNotFoundException;
import org.dealership.pipelinemanager.exceptions.SelfDependencyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            CycleDetectedException.class,
            NodeNotFoundException.class,
            SelfDependencyException.class,
            IllegalStateException.class
    })
    public ResponseEntity<Map<String, String>> handleBadRequestExceptions(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(PipelineNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(PipelineNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
