package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class BookingService {

    JpaBookingRepository jpaBookingRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    public Booking create(Booking booking, Long userId) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }
        if (booking.getEnd() == null) {
            log.info("Дата окончания бронирования не может быть null");
            throw new ValidateException("Дата окончания бронирования не может быть null");
        }
        if (booking.getStart() == null) {
            log.info("Дата начала бронирования не может быть null");
            throw new ValidateException("Дата начала бронирования не может быть null");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().isEqual(booking.getEnd())) {
            log.info("Дата окончания бронирования не может быть  раньше начала бронирования");
            throw new ValidateException("Дата окончания бронирования не может быть  раньше начала бронирования");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            log.info("Дата начала бронирования не может быть в прошлом");
            throw new ValidateException("Дата начала бронирования не может быть в прошлом");
        }
        if (booking.getItem() == null) {
            log.info("Такая вещь не найдена");
            throw new ValidateException("Такая вещь не найдена");
        }
        if (booking.getItem().getId() == null) {
            log.info("Такая вещь не найдена");
            throw new NotFoundException("Такая вещь не найдена");
        }
        if (booking.getItem().getAvailable() == null || booking.getItem().getAvailable() == false) {
            log.info("Вы не можете забронировать эту вещь");
            throw new ValidateException("Вы не можете забронировать эту вещь");
        }
        if (booking.getBooker() == null) {
            log.info("Такого пользователя нет");
            throw new ValidateException("Такого пользователя нет");
        }

        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        }

        return jpaBookingRepository.save(booking);
    }

    public Booking approve(Long bookingId, Long userId, Boolean approved) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }
        if (bookingId == null) {
            log.info("");
            throw new NotFoundException("");
        }
        Booking booking = findById(bookingId, userId);
        if (userId != booking.getItem().getOwner().getId()) {
            log.info("");
            throw new ForbiddenException("");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.info("");
            throw new ForbiddenException("");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return jpaBookingRepository.save(booking);
    }

    public Booking findById(Long bookingId, Long userId) {

        Booking booking = jpaBookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(""));
        if (isUserOwner(booking, userId) || isUserBooker(booking, userId)) {
            return booking;
        } else {
            log.info("");
            throw new ForbiddenException("");
        }

    }

    private boolean isUserBooker(Booking booking, Long userId) {
        return booking.getBooker().getId() == userId;
    }

    private boolean isUserOwner(Booking booking, Long userId) {

        return booking.getItem().getOwner().getId() == userId;
    }

    public List<Booking> findAllByUser(Long userId, String state) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }

    }


}
