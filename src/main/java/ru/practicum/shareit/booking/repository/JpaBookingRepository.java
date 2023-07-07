package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

public interface JpaBookingRepository  extends JpaRepository<Booking, Long> {
}
