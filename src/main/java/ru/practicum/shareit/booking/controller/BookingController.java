package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingResponseDto create(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId
    ) {

        Booking booking = BookingMapper.toBooking(
                bookingRequestDto,
                itemService.getById(bookingRequestDto.getItemId()),
                userService.getById(userId)
        );
        Booking createdBooking = bookingService.create(booking);
        return BookingMapper.toBookingResponseDto(createdBooking);
    }


    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam boolean approved
    ) {

        Booking booking = bookingService.approve(bookingId, userId, approved);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        Booking booking = bookingService.getById(bookingId, userId);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state
    ) {
        return bookingService.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state
    ) {
        return bookingService.getAllByOwner(userId, state);
    }
}
