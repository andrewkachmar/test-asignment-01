package com.teamvoy.teamvoytestasignment.exceptions;

import com.teamvoy.teamvoytestasignment.exceptions.models.ErrorResponseDto;
import com.teamvoy.teamvoytestasignment.exceptions.models.ResourceDuplicateException;
import com.teamvoy.teamvoytestasignment.exceptions.models.ResourceNotFoundException;
import com.teamvoy.teamvoytestasignment.exceptions.models.ValidationErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ResponseExceptionHandler {
    @ExceptionHandler(ResourceDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponseDto> handleResourceDuplicateException(ResourceDuplicateException ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorResponseDto.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorResponseDto.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDto> handleException(RuntimeException ex, HttpServletRequest request) {
        return new ResponseEntity<>(ErrorResponseDto.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorResponseDto>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> groupedErrorMessages = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        List<ValidationErrorResponseDto> validationErrorResponses = groupedErrorMessages.entrySet().stream()
                .map(entry -> new ValidationErrorResponseDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toUnmodifiableList());
        return new ResponseEntity<>(validationErrorResponses, HttpStatus.BAD_REQUEST);
    }
}
