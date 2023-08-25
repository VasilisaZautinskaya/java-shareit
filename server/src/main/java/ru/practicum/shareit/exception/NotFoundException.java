package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ShareItApplicationException {
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
