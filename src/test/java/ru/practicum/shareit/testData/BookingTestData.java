package ru.practicum.shareit.testData;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingTestData {
    public static Booking getBookingTwo(User booker, Item item) {
        return new Booking(2L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), item, booker, BookingStatus.WAITING);
     }

    public static Booking getBookingOne(User booker, Item item) {
        return new Booking(1L, LocalDateTime.now().minusHours(6), LocalDateTime.now().minusHours(2), item, booker, BookingStatus.APPROVED);
     }
}
