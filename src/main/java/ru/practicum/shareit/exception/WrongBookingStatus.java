package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class WrongBookingStatus extends RuntimeException {
    public WrongBookingStatus(String message) {
        super(String.format("Unknown state: %s", message));
    }
}
