package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ShareItApplicationException {
    public DuplicateEmailException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}