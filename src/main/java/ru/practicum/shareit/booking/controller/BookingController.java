package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public BookingResponseDto create(@RequestBody BookingRequestDto bookingRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {

        if (userId == null) {
            log.info("UserId не может быть null");
            throw new ValidateException("UserId не может быть null");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Не найден пользователь");
        }
        if (bookingRequestDto.getItemId() == null) {
            throw new ValidateException("Нет");
        }
        Item item = itemService.findById(bookingRequestDto.getItemId());
        if (item == null) {
            throw new NotFoundException("Нет");
        }

        if (bookingRequestDto.getEnd() == null) {
            throw new ValidateException("Нет");
        }
        if (bookingRequestDto.getStart() == null) {
            throw new ValidateException("Нет");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto,
                item,
                user);
        Booking createdBooking = bookingService.create(booking, userId);
        BookingResponseDto createdBookingRequestDto = BookingMapper.toBookingResponseDto(createdBooking);

        return createdBookingRequestDto;
    }

    @GetMapping("/{bookingId}")
    public BookingRequestDto getById(@PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingService.findById(bookingId, userId);
        BookingRequestDto getBookingRequestDto = BookingMapper.toBookingDto(booking);
        return getBookingRequestDto;
    }
}
