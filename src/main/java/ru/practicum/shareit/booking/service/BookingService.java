package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.ValidateException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    JpaBookingRepository jpaBookingRepository;

    public Booking create(Booking booking, Long userId) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new ValidateException("UserId не может быть null");
        }

        return jpaBookingRepository.save(booking);
    }
}
