package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingMapperTest {
    @Test
    public void testToBooking() {

        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Long itemId = 2L;
        Long bookerId = 3L;
        BookingStatus status = BookingStatus.WAITING;
        User user = new User(3L, "Name", "email@example.com");
        ItemRequest itemRequest = new ItemRequest(1L, "Мне нужна синяя термокружка", user, LocalDateTime.now());
        Item item = new Item(2L, "ItemName", "ItemDescription", user, itemRequest, true);

        BookingRequestDto bookingDto = new BookingRequestDto(bookingId, start, end, itemId, bookerId, status);

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);

        Assertions.assertThat(bookingId).isEqualTo(booking.getId());
        Assertions.assertThat(start).isEqualTo(booking.getStart());
        Assertions.assertThat(end).isEqualTo(booking.getEnd());
        Assertions.assertThat(itemId).isEqualTo(booking.getItem().getId());
        Assertions.assertThat(bookerId).isEqualTo(booking.getBooker().getId());
        Assertions.assertThat(status).isEqualTo(booking.getStatus());
    }

    @Test
    public void testToRequestDto() {

        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        Long itemId = 2L;
        Long bookerId = 3L;
        BookingStatus status = BookingStatus.WAITING;
        User user = new User(2L, "Name", "email@example.com");
        ItemRequest itemRequest = new ItemRequest(1L, "Мне нужна синяя термокружка", user, LocalDateTime.now());
        Item item = new Item(1L, "ItemName", "ItemDescription", user, itemRequest, true);


        BookingRequestDto bookingDto = new BookingRequestDto(bookingId, start, end, itemId, bookerId, status);
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);


        Assertions.assertThat(bookingId).isEqualTo(bookingDto.getId());
        Assertions.assertThat(start).isEqualTo(bookingDto.getStart());
        Assertions.assertThat(end).isEqualTo(bookingDto.getEnd());
        Assertions.assertThat(itemId).isEqualTo(bookingDto.getItemId());
        Assertions.assertThat(bookerId).isEqualTo(bookingDto.getBookerId());
        Assertions.assertThat(status).isEqualTo(booking.getStatus());
    }


}
