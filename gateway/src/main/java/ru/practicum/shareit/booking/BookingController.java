package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                          @PathVariable("bookingId") Long bookingId,
                                          @RequestParam @NotNull Boolean approved) {

        log.debug("Processing method approve with params: userId = {}, bookingId = {}, approved = {}", userId, bookingId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestHeader(X_SHARER_USER_ID) long bookerId,
                                                 @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, bookerId, from, size);
        return bookingClient.getAllByBookerId(bookerId, state, from, size);
    }

    @GetMapping({"/owner"})
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.debug("Bookings by owner with id {} requested", userId);
        return bookingClient.getAllByOwnerId(userId, state, from, size);
    }


    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                          @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
