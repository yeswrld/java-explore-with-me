package ru.practicum.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationExcep extends RuntimeException {
    public ValidationExcep(String message) {
        super(message);
    }
}
