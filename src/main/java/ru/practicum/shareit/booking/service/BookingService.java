package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    JpaBookingRepository jpaBookingRepository;
    ItemRepository itemRepository;
    InMemoryUserRepository inMemoryUserRepository;

    public Booking create(Booking booking, Long userId) {
        if (userId == null) {
            log.info("UserId не может быть null");
            throw new ValidateException("UserId не может быть null");
        }
        if (booking.getBooker() == null) {
            log.info("Пользователь не может быть null");
            throw new ValidateException("Пользователь не может быть null");
        }
        if (booking.getEnd().equals(0)){
            log.info("Время должно быть указано");
            throw new ValidateException("Время должно быть указано");
        }
        if (booking.getItem() == null){
            log.info("Вещь не может быть null");
            throw new ValidateException("Вещь не может быть null");
        }
        User user = inMemoryUserRepository.findById(userId);
        if (user == null) {
            log.info("Такой пользователь не найден");
            throw new NotFoundException("Такой пользователь не найден");
        }


        return jpaBookingRepository.save(booking);
    }
}
