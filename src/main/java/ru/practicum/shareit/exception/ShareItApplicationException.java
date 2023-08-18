package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public abstract class ShareItApplicationException extends RuntimeException {
    public ShareItApplicationException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();
}
