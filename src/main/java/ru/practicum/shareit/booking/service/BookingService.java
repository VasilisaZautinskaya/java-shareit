package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.exception.WrongBookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class BookingService {

    private final JpaBookingRepository bookingRepository;
    private final UserService userService;

    public Booking create(Booking booking) {

        validateThatBookerIsNotOwner(booking.getItem(), booking.getBooker());
        validateThatDateEndIsAfterDateStart(booking);
        validateThatItemIsAvailable(booking);
        setDefauldStatusIfRequired(booking);

        return bookingRepository.save(booking);
    }

    private void setDefauldStatusIfRequired(Booking booking) {
        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        }
    }

    private void validateThatItemIsAvailable(Booking booking) {
        if (booking.getItem().getAvailable() == null || !booking.getItem().getAvailable()) {
            log.info("Вы не можете забронировать эту вещь");
            throw new ValidateException("Вы не можете забронировать эту вещь");
        }
    }

    private void validateThatDateEndIsAfterDateStart(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().isEqual(booking.getEnd())) {
            log.info("Дата окончания бронирования не может быть  раньше начала бронирования");
            throw new ValidateException("Дата окончания бронирования не может быть  раньше начала бронирования");
        }
    }


    public Booking approve(Long bookingId, Long userId, Boolean approved) {

        Booking booking = getById(bookingId, userId);
        valildateThatUserIsOwner(userId, booking);
        validateThatStatusIsFitForApprove(booking);

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    private void valildateThatUserIsOwner(Long userId, Booking booking) {
        if (!isUserOwner(booking, userId)) {
            log.info("Не найдено бронирование");
            throw new NotFoundException("Не найдено бронирование");
        }
    }

    private void validateThatStatusIsFitForApprove(Booking booking) {
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.info("Статус не может быть изменён");
            throw new ValidateException("Статус не может быть изменён");
        }
    }

    public Booking getById(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Не найдено бронирование"));
        if (isUserOwner(booking, userId) || isUserBooker(booking, userId)) {
            return booking;
        } else {
            log.info("Бронирование не найдено для пользователя");
            throw new NotFoundException("Бронирование не найдено для пользователя");
        }
    }

    private boolean isUserBooker(Booking booking, Long userId) {
        return Objects.equals(booking.getBooker().getId(), userId);
    }

    private boolean isUserOwner(Booking booking, Long userId) {
        return Objects.equals(booking.getItem().getOwner().getId(), userId);
    }


    public List<Booking> findAllByBooker(Long userId) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }
        return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
    }

    public List<Booking> findAllByBooker(Long userId, int from, int size) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByBookerIdOrderByStartDesc(
                userId,
                PageRequest.of(pageNum, size)
        );
    }

    public List<BookingResponseDto> findAllByOwner(Long userId, String stateStr, int from, int size) {
        userService.findById(userId);
        State state = getState(stateStr);
        switch (state) {
            case ALL:
                return BookingMapper.toBookingResponseListDto(findAllByOwner(userId, from, size));
            case CURRENT:
                return BookingMapper.toBookingResponseListDto(findAllCurrentByOwner(userId, from, size));
            case PAST:
                return BookingMapper.toBookingResponseListDto(findAllPastByOwner(userId, from, size));
            case FUTURE:
                return BookingMapper.toBookingResponseListDto(findAllFutureByOwner(userId, from, size));
            case WAITING:
                return BookingMapper.toBookingResponseListDto(findAllWaitingByOwner(userId, from, size));
            case REJECTED:
                return BookingMapper.toBookingResponseListDto(findAllRejectedByOwner(userId, from, size));
            default:
                throw new WrongBookingStatus(stateStr);
        }

    }

    public List<BookingResponseDto> findAllBookings(Long userId, String stateStr, int from, int size) {
        userService.findById(userId);
        State state = getState(stateStr);
        switch (state) {
            case ALL:
                return BookingMapper.toBookingResponseListDto(findAllByBooker(userId, from, size));
            case CURRENT:
                return BookingMapper.toBookingResponseListDto(findAllCurrentByBooker(userId, from, size));
            case PAST:
                return BookingMapper.toBookingResponseListDto(findAllPastByBooker(userId, from, size));
            case FUTURE:
                return BookingMapper.toBookingResponseListDto(findAllFutureByBooker(userId, from, size));
            case WAITING:
                return BookingMapper.toBookingResponseListDto(findAllWaitingByBooker(userId, from, size));
            case REJECTED:
                return BookingMapper.toBookingResponseListDto(findAllRejectedByBooker(userId, from, size));
            default:
                throw new WrongBookingStatus(stateStr);
        }
    }

    private State getState(String stateStr) {
        try {
            return State.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            throw new WrongBookingStatus(stateStr);
        }
    }

    public void validateThatBookerIsNotOwner(Item item, User booker) {
        if (Objects.equals(item.getOwner().getId(), booker.getId())) {
            log.info("Вы не можете арендовать свою вещь");
            throw new NotFoundException("Вы не можете арендовать свою вещь");
        }
    }


    public List<Booking> findAllCurrentByBooker(Long userId, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new IllegalArgumentException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                userId,
                now,
                now,
                PageRequest.of(pageNum, size));
    }


    public List<Booking> findAllPastByBooker(Long userId, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new IllegalArgumentException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now, PageRequest.of(pageNum, size));
    }


    public List<Booking> findAllFutureByBooker(Long userId, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                userId,
                now,
                PageRequest.of(pageNum, size));
    }


    public List<Booking> findAllWaitingByBooker(Long userId, int from, int size) {
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                userId,
                BookingStatus.WAITING,
                PageRequest.of(pageNum, size)
        );
    }



    public List<Booking> findAllRejectedByBooker(Long userId, int from, int size) {
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                userId,
                BookingStatus.REJECTED,
                PageRequest.of(pageNum, size)
        );
    }



    public List<Booking> findAllByOwner(Long ownerId, int from, int size) {
        if (ownerId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                ownerId,
                PageRequest.of(pageNum, size)
        );
    }




    public List<Booking> findAllCurrentByOwner(Long ownerId, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                ownerId,
                now,
                now,
                PageRequest.of(pageNum, size)
        );
    }



    public List<Booking> findAllPastByOwner(Long ownerId, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, PageRequest.of(pageNum, size));
    }


    public List<Booking> findAllFutureByOwner(Long ownerId, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, PageRequest.of(pageNum, size));
    }



    public List<Booking> findAllWaitingByOwner(Long ownerId, int from, int size) {
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new IllegalArgumentException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartAsc(
                ownerId,
                BookingStatus.WAITING,
                PageRequest.of(pageNum, size)
        );
    }


    public List<Booking> findAllRejectedByOwner(Long ownerId, int from, int size) {
        if (size < 1 || from < 0) {
            log.info("Неверный номер страни");
            throw new ValidateException("Wrong page number");
        }
        int pageNum = from / size;
        return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartAsc(ownerId, BookingStatus.REJECTED, PageRequest.of(pageNum, size));
    }

    public Booking getLastBookingForItem(Long itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        return bookings.stream()
                .filter(o -> o.getStart().isBefore(LocalDateTime.now()))
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart()))
                .findFirst()
                .orElse(null);

    }

    public Booking getNextBookingForItem(Long itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        return bookings.stream()
                .filter(o -> o.getStart().isAfter(LocalDateTime.now()) && !o.getStatus().equals(BookingStatus.REJECTED))
                .sorted((o1, o2) -> o1.getStart().compareTo(o2.getStart()))
                .findFirst()
                .orElse(null);
    }

    public boolean isUserBookedItem(User user, Item item) {
        List<Booking> bookings = findAllByBooker(user.getId());
        return bookings.stream()
                .anyMatch(o -> Objects.equals(o.getItem().getId(), item.getId())
                        && o.getStatus().equals(BookingStatus.APPROVED)
                        && o.getEnd().isBefore(LocalDateTime.now()));
    }
}
