package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
@Primary
public class DbUserRepository implements UserRepository {
    @Autowired
    JpaUserRepository jpaUserRepository;


    @Override
    public User findById(Long userId) {
        return jpaUserRepository.findById(userId).orElse(null);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }

    @Override
    public void deleteById(long userId) {
        jpaUserRepository.deleteById(userId);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }
}
