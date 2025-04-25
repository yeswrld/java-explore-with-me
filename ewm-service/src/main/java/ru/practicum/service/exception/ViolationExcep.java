package ru.practicum.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ViolationExcep extends RuntimeException {
    public ViolationExcep(String message) {
        super(message);
    }
}
