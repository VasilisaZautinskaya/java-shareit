package ru.practicum.shareit.user.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User update(Long userId, User user) {

        validateThatEmailIsFree(userId, user);

        User oldUser = findById(userId);

        if (userId == null) {
            return user;
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }

        return userRepository.save(oldUser);
    }

    private void validateThatEmailIsFree(Long userId, User user) {
        User userWithSameEmail = userRepository.getUserByEmail(user.getEmail());
        if (userWithSameEmail != (null) && !userWithSameEmail.getId().equals(userId)) {
            log.info("Такой email уже существует");
            throw new DuplicateEmailException("Пользователь с таким email уже зарегистрирован");
        }
    }


    public void remove(long userId) {
        userRepository.deleteById(userId);
    }


    public User findById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            log.info("Пользователь не найден");
            throw new NotFoundException("Не найден пользователь");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
