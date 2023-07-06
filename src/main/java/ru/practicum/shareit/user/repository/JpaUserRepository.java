package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface JpaUserRepository extends JpaRepository<User, Long> {

    List<User> findAll();
    User findByEmail(String email);

    void deleteById(Long userId);
}
