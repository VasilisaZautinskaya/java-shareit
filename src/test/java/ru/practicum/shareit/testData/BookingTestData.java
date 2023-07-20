package ru.practicum.shareit.testData;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingTestData {
    public static Booking getBookingTwo(User userComment, Item item) {
         Booking nextBooking = new Booking(2L, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), item, userComment, BookingStatus.WAITING);
         return nextBooking;
     }

    public static Booking getBookingOne(User userComment, Item item) {
         Booking lastBooking = new Booking(1L, LocalDateTime.now().minusHours(6), LocalDateTime.now().minusHours(2), item, userComment, BookingStatus.APPROVED);
         return lastBooking;
     }
}
