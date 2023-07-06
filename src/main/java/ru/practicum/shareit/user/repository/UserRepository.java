package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;



public interface UserRepository {

    User findById(Long userId);

    User save(User user);

    void deleteById(long userId);

    List<User> findAll();

    User getUserByEmail(String email);

}



