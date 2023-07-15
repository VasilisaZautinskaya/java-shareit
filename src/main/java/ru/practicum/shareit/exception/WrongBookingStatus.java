package ru.practicum.shareit.exception;


public class WrongBookingStatus extends RuntimeException {
    public WrongBookingStatus(String message) {
        super(String.format("Unknown state: %s", message));
    }
}
