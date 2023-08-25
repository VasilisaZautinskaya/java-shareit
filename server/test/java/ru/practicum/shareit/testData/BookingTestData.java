package ru.practicum.shareit.testData;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingTestData {
    public static Booking getBookingTwo(User booker, Item item) {
        return new Booking(2L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), item, booker, BookingStatus.WAITING);
    }

    public static Booking getBookingOne(User booker, Item item) {
        return new Booking(1L, LocalDateTime.now().minusHours(6), LocalDateTime.now().minusHours(2), item, booker, BookingStatus.APPROVED);
    }

    public static List<Booking> createBookingList(User booker, Item item) {
        List<Booking> bookingList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        bookingList.add(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusHours(1), item, booker, BookingStatus.WAITING));
        bookingList.add(new Booking(2L, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), item, booker, BookingStatus.APPROVED));
        bookingList.add(new Booking(3L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, booker, BookingStatus.REJECTED));
        bookingList.add(new Booking(4L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, booker, BookingStatus.CANCELED));
        bookingList.add(new Booking(5L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), item, booker, BookingStatus.WAITING));

        return bookingList;
    }
}
