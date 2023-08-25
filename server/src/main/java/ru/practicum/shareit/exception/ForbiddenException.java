package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;

public class ForbiddenException extends ShareItApplicationException {
    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
