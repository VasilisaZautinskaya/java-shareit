package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.DbUserRepository;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class UserRepositoryTest {

    @Mock
    JpaUserRepository jpaUserRepository;

    @InjectMocks
    DbUserRepository dbUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() {
        Long userId = 1L;
        User user = UserTestData.getUserOne();

        when(jpaUserRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUser = Optional.of(dbUserRepository.findById(userId));

        Assertions.assertThat(foundUser.get().getId()).isEqualTo(userId);
        Assertions.assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        Assertions.assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testSave() {
        User user = UserTestData.getUserOne();
        when(jpaUserRepository.save(user)).thenReturn(user);
        User savedUser = dbUserRepository.save(user);

        Assertions.assertThat(savedUser.getId()).isEqualTo(user.getId());
        Assertions.assertThat(savedUser.getName()).isEqualTo(user.getName());
        Assertions.assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }


    @Test
    void itShouldReturnTwoUsers() {
        User user = UserTestData.getUserOne();
        List<User> userList = new ArrayList<>();
        userList.add(user);

        User user2 = UserTestData.getUserTwo();
        userList.add(user2);
        dbUserRepository.save(user);
        dbUserRepository.save(user2);

        when(jpaUserRepository.findAll()).thenReturn(userList);

        List<User> findUser = dbUserRepository.findAll();
        Assertions.assertThat(findUser.size()).isEqualTo(2);

    }

    @Test
    void itShouldDeleteById() {
        User user = UserTestData.getUserOne();
        when(jpaUserRepository.save(user)).thenReturn(user);
        dbUserRepository.save(user);
        dbUserRepository.deleteById(user.getId());
        Assertions.assertThat(dbUserRepository.findById(user.getId())).isNull();
    }

    @Test
    void testGetByEmail() {
        User user = UserTestData.getUserOne();

        when(jpaUserRepository.findByEmail(user.getEmail())).thenReturn(user);

        User getUser = dbUserRepository.getUserByEmail(user.getEmail());

        Assertions.assertThat(getUser.getId()).isEqualTo(user.getId());
        Assertions.assertThat(getUser.getName()).isEqualTo(user.getName());
        Assertions.assertThat(getUser.getEmail()).isEqualTo(user.getEmail());
    }


}
