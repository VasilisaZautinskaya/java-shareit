package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.Service.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingDto create(@RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {


        Booking booking = BookingMapper.toBooking(bookingDto,
                itemService.findById(bookingDto.getItemId()),
                userService.getUserById(userId));
        Booking createdBooking = bookingService.create(booking, userId);
        BookingDto createdBookingDto = BookingMapper.toBookingDto(createdBooking);
        return createdBookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingService.findById(bookingId, userId);
        BookingDto getBookingDto = BookingMapper.toBookingDto(booking);
        return getBookingDto;
    }
}
