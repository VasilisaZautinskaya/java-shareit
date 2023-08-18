package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;

public class WrongBookingStatus extends ShareItApplicationException {
    public WrongBookingStatus(String message) {
        super(String.format("Unknown state: %s", message));
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
