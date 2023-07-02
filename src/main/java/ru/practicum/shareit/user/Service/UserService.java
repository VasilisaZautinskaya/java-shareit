package ru.practicum.shareit.user.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    UserRepository userRepository;

    public User createUser(User user) {
        if (userRepository.getUserByEmail(user.getEmail()) != (null)) {
            log.info("Такой email уже существует");
            throw new DuplicateEmailException("Пользователь с таким email уже зарегистрирован");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.info("Неверный email");
            throw new ValidateException("Неверный email");
        }

        return userRepository.save(user);
    }

    public User update(Long userId, User user) {
        User oldUser = userRepository.getUserByEmail(user.getEmail());
        if (oldUser != (null) && !oldUser.getId().equals(userId)) {
            log.info("Такой email уже существует");
            throw new DuplicateEmailException("Пользователь с таким email уже зарегистрирован");
        }
        return userRepository.updateUser(userId, user);
    }

    public void remove(long userId) {
        userRepository.remove(userId);

    }


    public User getUserById(Long userId) {
        return userRepository.getUser(userId);
    }

    private void validateNewUser(User user) {

        if (user.getId() == null) {
            log.error("Пользователь с таким id не найден");
            throw new ValidateException("Неправильный id пользователя");
        }
        if (user.getEmail() == null) {
            log.error("Пользователь с таким Email не найден");
            throw new ValidateException("Неправильный email пользователя");
        }

        if (user.getName() == null) {
            log.error("Неправильное имя пользователя");
            throw new ValidateException("Неправильное имя пользователя");
        }
    }


    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
