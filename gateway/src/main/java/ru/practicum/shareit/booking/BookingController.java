package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<Object> getAllBooking(@NotNull @RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", stateParam)));
        log.info("Processing method getAllBookings with params: userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);
        return bookingClient.getAllBooking(userId, state, from, size);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@NotNull @RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown state: %s", stateParam)));
        log.info("Processing method getAllByOwner with params: userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);
        return bookingClient.getAllByOwner(userId, state, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> bookItem(@NotNull @RequestHeader(X_SHARER_USER_ID) Long userId,
                                           @RequestBody @Valid BookingDto requestDto) {
        log.info("Proccesing method bookItem with params: booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                          @PathVariable Long bookingId) {
        log.info("Processing method getById with params: userId = {}, bookingId = {}", userId, bookingId);
        return bookingClient.getById(userId, bookingId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId,
                                          @NotNull @RequestParam(name = "approved") Boolean approved,
                                          @NotNull @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Processing method approve with params: userId = {}, bookingId = {}, approved = {}", userId, bookingId, approved);
        return bookingClient.approve(bookingId, approved, userId);
    }
}