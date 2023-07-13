package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;



@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public BookingResponseDto create( @RequestBody BookingRequestDto bookingRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {

        User user = bookingService.validateAndGetUser(userId);
        Item item = bookingService.validateAndGetItem(bookingRequestDto.getItemId());

         bookingService.validateCreateBooking(bookingRequestDto, item, user);

        Booking booking = BookingMapper.toBooking(bookingRequestDto,
                item,
                user);
        Booking createdBooking = bookingService.create(booking, userId);
        BookingResponseDto createdBookingRequestDto = BookingMapper.toBookingResponseDto(createdBooking);

        return createdBookingRequestDto;
    }


    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam boolean approved) {

        bookingService.validateAndGetUser(userId);

        Booking booking = bookingService.approve(bookingId, userId, approved);
        BookingResponseDto createdBookingRequestDto = BookingMapper.toBookingResponseDto(booking);

        return createdBookingRequestDto;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        bookingService.validateAndGetUser(userId);
        Booking booking = bookingService.findById(bookingId, userId);
        BookingResponseDto createdBookingRequestDto = BookingMapper.toBookingResponseDto(booking);

        return createdBookingRequestDto;
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") State state) {

        return bookingService.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, defaultValue = "ALL") State state) {

        return bookingService.getAllByOwner(userId, state);
    }
}
