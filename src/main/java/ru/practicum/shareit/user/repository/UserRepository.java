package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Repository
public interface UserRepository {

    long generatedId();

    User getUser(Long userId);

    User save(User user);

    User updateUser(Long userId, User user);

    void remove(long userId);

    List<User> getAllUsers();

    User getUserByEmail(String email);

}



