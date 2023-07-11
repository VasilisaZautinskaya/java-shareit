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
            throw new NotFoundException("Такая вещь не найдена");
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
            log.info("Не найдено бронирование");
            throw new NotFoundException("Не найдено бронирование");
        }
        Booking booking = findById(bookingId, userId);
        if (userId != booking.getItem().getOwner().getId()) {
            log.info("Не найдено бронирование");
            throw new NotFoundException("Не найдено бронирование");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.info("Статус не может быть изменён");
            throw new ValidateException("Статус не может быть изменён");
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
            log.info("Бронирование не найдено для пользователя");
            throw new NotFoundException("Бронирование не найдено для пользователя");
        }

    }

    private boolean isUserBooker(Booking booking, Long userId) {
        return booking.getBooker().getId() == userId;
    }

    private boolean isUserOwner(Booking booking, Long userId) {

        return booking.getItem().getOwner().getId() == userId;
    }

    public List<Booking> findAllByBooker(Long userId) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }
        return jpaBookingRepository.findAllByBookerIdOrderByStartDesc(userId);
    }

    public List<Booking> findAllCurrentByBooker(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return jpaBookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
    }

    public List<Booking> findAllPastByBooker(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return jpaBookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
    }

    public List<Booking> findAllFutureByBooker(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return jpaBookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
    }

    public List<Booking> findAllWaitingByBooker(Long userId) {
        return jpaBookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
    }

    public List<Booking> findAllRejectedByBooker(Long userId) {
        return jpaBookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
    }

    public List<Booking> findAllByOwner(Long ownerId) {
        if (ownerId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }
        return jpaBookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
    }

    public List<Booking> findAllCurrentByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return jpaBookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
    }

    public List<Booking> findAllPastByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return jpaBookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
    }

    public List<Booking> findAllFutureByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return jpaBookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
    }

    public List<Booking> findAllWaitingByOwner(Long ownerId) {
        return jpaBookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
    }

    public List<Booking> findAllRejectedByOwner(Long ownerId) {
        return jpaBookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
    }


}
