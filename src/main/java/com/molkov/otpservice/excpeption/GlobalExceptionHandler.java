package com.molkov.otpservice.excpeption;

import com.molkov.otpservice.dto.error.ErrorField;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.molkov.otpservice.dto.error.ErrorResponse;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception, WebRequest request) {
        return buildErrorMessage(exception, request);
    }

    @ExceptionHandler({OtpCodeExpiredException.class, OtpCodeNotActiveException.class})
    @ResponseStatus(HttpStatus.GONE)
    public ErrorResponse handleOtpCodeNotActiveException(Exception exception, WebRequest request) {
        return buildErrorMessage(exception, request);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityExistsException(EntityExistsException exception, WebRequest request) {
        return buildErrorMessage(exception, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception, WebRequest request) {
        List<ErrorField> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorField(error.getField(), error.getDefaultMessage()))
                .toList();

        return ErrorResponse.builder()
                .message("Validation failed")
                .path(getPath(request))
                .details(validationErrors)
                .build();
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception exception, WebRequest request) {
        return buildErrorMessage(exception, request);
    }

    private ErrorResponse buildErrorMessage(Exception exception, WebRequest request) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .path(getPath(request))
                .build();
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
