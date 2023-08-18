package ru.practicum.shareit.booking.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.Service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingResponseDto create(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader(value = X_SHARER_USER_ID) Long userId
    ) {

        log.info("Processing method create with params: userId = {}, bookingRequestDto = {}", userId, bookingRequestDto);

        Booking booking = BookingMapper.toBooking(
                bookingRequestDto,
                itemService.findById(bookingRequestDto.getItemId()),
                userService.findById(userId)
        );
        Booking createdBooking = bookingService.create(booking);
        return BookingMapper.toBookingResponseDto(createdBooking);
    }


    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @PathVariable Long bookingId,
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam boolean approved
    ) {
        log.info("Processing method approve with params: userId = {}, bookingId = {}, approved = {}", userId, bookingId, approved);
        Booking booking = bookingService.approve(bookingId, userId, approved);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @PathVariable Long bookingId,
            @RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        log.info("Processing method getById with params: userId = {}, bookingId = {}", userId, bookingId);
        Booking booking = bookingService.getById(bookingId, userId);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Processing method getAllBookings with params: userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);
        return bookingService.findAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwner(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Processing method getAllByOwner with params: userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);
        return bookingService.findAllByOwner(userId, state, from, size);
    }
}
