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

        User user = validateAndGetUser(userId);
        Item item = validateAndGetItem(bookingRequestDto.getItemId());

        validateCreateBooking(bookingRequestDto);

        Booking booking = BookingMapper.toBooking(bookingRequestDto,
                item,
                user);
        Booking createdBooking = bookingService.create(booking, userId);
        BookingResponseDto createdBookingRequestDto = BookingMapper.toBookingResponseDto(createdBooking);

        return createdBookingRequestDto;
    }

    private void validateCreateBooking(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd() == null) {
            log.info("Время окончания бронирования не указано");
            throw new ValidateException("Время окончания бронирования не указано");
        }
        if (bookingRequestDto.getStart() == null) {
            log.info("Время начала бронирования не указано");
            throw new ValidateException("Время начала бронирования не указано");
        }
    }

    private Item validateAndGetItem(Long itemId) {
        if (itemId == null) {
            log.info("Вещь с таким id  не найдена");
            throw new ValidateException("Вещь с таким id  не найдена");
        }
        Item item = itemService.findById(itemId);
        if (item == null) {
            log.info("Вещь не найдена");
            throw new NotFoundException("Вещь не найдена");
        }
        return item;
    }

    private User validateAndGetUser(Long userId) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new ValidateException("UserId не может быть null");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Не найден пользователь");
        }
        return user;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam boolean approved) {

        validateAndGetUser(userId);
        if (bookingId == null) {
            log.info("");
            throw new ValidateException("");
        }
        Booking booking = bookingService.approve(bookingId, userId, approved);
        BookingResponseDto createdBookingRequestDto = BookingMapper.toBookingResponseDto(booking);

        return createdBookingRequestDto;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        validateAndGetUser(userId);
        Booking booking = bookingService.findById(bookingId, userId);
        BookingResponseDto createdBookingRequestDto = BookingMapper.toBookingResponseDto(booking);

        return createdBookingRequestDto;
    }
}
