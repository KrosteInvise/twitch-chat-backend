package com.kroste.twitchchatbackend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        int statusCode = e.getStatusCode().value();

        ErrorResponse errorBody = new ErrorResponse(
                statusCode,
                e.getStatusCode().toString(),
                e.getReason(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorBody, e.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
        ErrorResponse errorBody = new ErrorResponse(
                500,
                "Internal Server Error",
                "Something going wrong: " + e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.internalServerError().body(errorBody);
    }
}