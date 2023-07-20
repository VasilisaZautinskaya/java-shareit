package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingMapperTest {
    @Test
    public void testToBooking() {

        User userRequester = UserTestData.getUserOne();
        User userOwner = UserTestData.getUserTwo();
        ItemRequest itemRequest = getItemRequest(userRequester);
        Item item = ItemTestData.getItemOne(itemRequest, userOwner);
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        BookingRequestDto bookingDto = new BookingRequestDto(bookingId, start, end, item.getId(), userRequester.getId(), BookingStatus.WAITING);

        Booking booking = BookingMapper.toBooking(bookingDto, item, userRequester);

        Assertions.assertThat(bookingId).isEqualTo(booking.getId());
        Assertions.assertThat(start).isEqualTo(booking.getStart());
        Assertions.assertThat(end).isEqualTo(booking.getEnd());
        Assertions.assertThat(item.getId()).isEqualTo(booking.getItem().getId());
        Assertions.assertThat(userRequester.getId()).isEqualTo(booking.getBooker().getId());
        Assertions.assertThat(BookingStatus.WAITING).isEqualTo(booking.getStatus());
    }

    @Test
    void toBookingResponseDto() {
        Long bookingId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        User user = UserTestData.getUserOne();
        ItemRequest itemRequest = getItemRequest(user);
        Item item = new Item(2L, "ItemName", "ItemDescription", user, itemRequest, true);
        BookingStatus status = BookingStatus.WAITING;
        Booking booking = new Booking(bookingId, start, end, item, user, status);

        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(booking);

        Assertions.assertThat(bookingResponseDto.getId()).isEqualTo(booking.getId());
        Assertions.assertThat(bookingResponseDto.getStart()).isEqualTo(booking.getStart());
        Assertions.assertThat(bookingResponseDto.getEnd()).isEqualTo(booking.getEnd());
        Assertions.assertThat(bookingResponseDto.getItem().getId()).isEqualTo(booking.getItem().getId());
        Assertions.assertThat(bookingResponseDto.getBooker().getId()).isEqualTo(booking.getBooker().getId());
        Assertions.assertThat(bookingResponseDto.getStatus()).isEqualTo(booking.getStatus());
    }

    public static ItemRequest getItemRequest(User user) {
        ItemRequest itemRequest = new ItemRequest(1L, "Мне нужна синяя термокружка", user, LocalDateTime.now());
        return itemRequest;
    }

    @Test
    void toBookingResponseDtoNull() {
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingResponseDto(null);
        Assertions.assertThat(bookingResponseDto).isNull();
    }

    @Test
    void toBookingResponseListDto() {
//        List<Booking> bookingList = new ArrayList<>();
//        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusHours(6), LocalDateTime.now().minusHours(2), item, user, BookingStatus.APPROVED);
//        Booking nextBooking = new Booking(2L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), item, user, BookingStatus.WAITING);
    }

    @Test
    void mapToBookingForItemDto() {
    }

}
