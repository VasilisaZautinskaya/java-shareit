package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@AllArgsConstructor
public class BookingService {

    private final JpaBookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


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
        if (booking.getItem().getAvailable() == null || !booking.getItem().getAvailable()) {
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

        return bookingRepository.save(booking);
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
        if (!userId.equals(booking.getItem().getOwner().getId())) {
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


        return bookingRepository.save(booking);
    }

    public Booking findById(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(""));
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

    public List<BookingResponseDto> getAllByOwner(Long userId, State state) {
        validateAndGetUser(userId);
        switch (state) {
            case ALL:
                return BookingMapper.toBookingResponseListDto(findAllByOwner(userId));
            case CURRENT:
                return BookingMapper.toBookingResponseListDto(findAllCurrentByOwner(userId));
            case PAST:
                return BookingMapper.toBookingResponseListDto(findAllPastByOwner(userId));
            case FUTURE:
                return BookingMapper.toBookingResponseListDto(findAllFutureByOwner(userId));
            case WAITING:
                return BookingMapper.toBookingResponseListDto(findAllWaitingByOwner(userId));
            case REJECTED:
                return BookingMapper.toBookingResponseListDto(findAllRejectedByOwner(userId));
            default:
                throw new WrongBookingStatus(state.toString());
        }

    }

    public List<BookingResponseDto> getAllBookings(Long userId, State state) {
        validateAndGetUser(userId);
        switch (state) {
            case ALL:
                return BookingMapper.toBookingResponseListDto(findAllByBooker(userId));
            case CURRENT:
                return BookingMapper.toBookingResponseListDto(findAllCurrentByBooker(userId));
            case PAST:
                return BookingMapper.toBookingResponseListDto(findAllPastByBooker(userId));
            case FUTURE:
                return BookingMapper.toBookingResponseListDto(findAllFutureByBooker(userId));
            case WAITING:
                return BookingMapper.toBookingResponseListDto(findAllWaitingByBooker(userId));
            case REJECTED:
                return BookingMapper.toBookingResponseListDto(findAllRejectedByBooker(userId));
            default:
                throw new WrongBookingStatus(state.toString());
        }
    }

    public void validateCreateBooking(BookingRequestDto bookingRequestDto, Item item, User user) {
        if (bookingRequestDto.getEnd() == null) {
            log.info("Время окончания бронирования не указано");
            throw new ValidateException("Время окончания бронирования не указано");
        }
        if (bookingRequestDto.getStart() == null) {
            log.info("Время начала бронирования не указано");
            throw new ValidateException("Время начала бронирования не указано");
        }
        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            log.info("Вы не можете арендовать свою вещь");
            throw new NotFoundException("Вы не можете арендовать свою вещь");
        }
    }


    public List<Booking> findAllCurrentByBooker(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
    }

    public List<Booking> findAllPastByBooker(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
    }

    public List<Booking> findAllFutureByBooker(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
    }

    public List<Booking> findAllWaitingByBooker(Long userId) {
        return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
    }

    public List<Booking> findAllRejectedByBooker(Long userId) {
        return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
    }

    public List<Booking> findAllByOwner(Long ownerId) {
        if (ownerId == null) {
            log.info("UserId не может быть null");
            throw new NotFoundException("UserId не может быть null");
        }
        return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
    }


    public List<Booking> findAllCurrentByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
    }

    public List<Booking> findAllPastByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
    }

    public List<Booking> findAllFutureByOwner(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
    }

    public List<Booking> findAllWaitingByOwner(Long ownerId) {
        return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
    }

    public List<Booking> findAllRejectedByOwner(Long ownerId) {
        return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
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

    public Item validateAndGetItem(Long itemId) {
        if (itemId == null) {
            log.info("Вещь с таким id  не найдена");
            throw new ValidateException("Вещь с таким id  не найдена");
        }
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            log.info("Вещь не найдена");
            throw new NotFoundException("Вещь не найдена");
        }
        return item;
    }

    public User validateAndGetUser(Long userId) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new ValidateException("UserId не может быть null");
        }
        User user = userRepository.findById(userId);
        if (user == null) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Не найден пользователь");
        }
        return user;
    }

}
