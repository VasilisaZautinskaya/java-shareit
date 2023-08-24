package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;

public class ValidateException extends ShareItApplicationException {
    public ValidateException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
