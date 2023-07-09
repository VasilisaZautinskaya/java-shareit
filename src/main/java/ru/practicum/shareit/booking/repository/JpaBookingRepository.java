package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface JpaBookingRepository  extends JpaRepository<Booking, Long> {

    Collection<Object> findAllByItemIdAndBookerIdAndStatusEqualsAndEndIsBefore(Long itemId,
                                                                               Long userId,
                                                                               BookingStatus approved,
                                                                               LocalDateTime now);
}
