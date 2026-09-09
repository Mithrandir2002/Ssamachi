package com.earthquake.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DuplicateUsernameException.class, DuplicateEmailException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError onDuplicate(RuntimeException e) {
        return ApiError.of(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onInvalidVerificationCode(InvalidVerificationCodeException e) {
        // Deliberately vague: the caller must not learn whether the code was wrong
        // or whether no pending registration exists for that address.
        return ApiError.of(HttpStatus.BAD_REQUEST.value(), "Mã xác thực không đúng hoặc đã hết hạn.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError onValidationError(MethodArgumentNotValidException e) {
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(this::describe)
                .collect(Collectors.joining("; "));
        return ApiError.of(HttpStatus.BAD_REQUEST.value(), details);
    }

    private String describe(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
