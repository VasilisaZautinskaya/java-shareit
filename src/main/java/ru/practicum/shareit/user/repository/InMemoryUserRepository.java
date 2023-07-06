package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
public class InMemoryUserRepository implements UserRepository {
    HashMap<Long, User> users = new HashMap<>();
    long idSequence;

    @Override
    public User findById(Long userId) {

        return users.get(userId);
    }

    @Override
    public void deleteById(long userId) {
        users.remove(userId);

    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        Long newId = generatedId();
        if (user.getId() == null) {
            user.setId(newId);
        }
        users.put(newId, user);
        return user;

    }

    @Override
    public User getUserByEmail(String email) {
        for (User user : users.values()
        ) {
            if (!user.getEmail().isEmpty()) {
                if (user.getEmail().equals(email)) {
                    return user;
                }
            }
        }
        return null;
    }


    public long generatedId() {
        return ++idSequence;
    }
}
