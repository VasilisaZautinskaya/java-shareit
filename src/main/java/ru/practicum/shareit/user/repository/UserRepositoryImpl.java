package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {
    HashMap<Long, User> users = new HashMap<>();
    long idSequence;

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public void remove(long userId) {
        users.remove(userId);

    }


    @Override
    public User updateUser(Long userId, User user) {
        User oldUser = getUser(userId);
        if (userId == null) {
            return user;
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }


        users.put(userId, oldUser);
        return oldUser;

    }


    @Override
    public List<User> getAllUsers() {
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

    @Override
    public long generatedId() {
        return ++idSequence;
    }
}
