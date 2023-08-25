package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void creteUser() {
        User user = new User(null, "Name", "email@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User created = userService.createUser(user);

        Assertions.assertThat(user).isEqualTo(created);
        Assertions.assertThat(user).isNotNull();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserDuplicateEmail() {
        User oldUser = UserTestData.getUserOne();
        String email = "dodo@example.com";
        oldUser.setEmail(email);
        when(userRepository.getUserByEmail(email)).thenReturn(oldUser);
        User user = UserTestData.getUserTwo();
        user.setEmail("dodo@example.com");

        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> userService.update(user.getId(), user));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Пользователь с таким email уже зарегистрирован")
                .asInstanceOf(InstanceOfAssertFactories.type(DuplicateEmailException.class));
    }

    @Test
    void updateUserNullId() {
        User user = UserTestData.getUserOne();
        RuntimeException throwable = (RuntimeException) Assertions.catchThrowable(() -> userService.update(user.getId(), user));

        Assertions.assertThat(throwable)
                .hasMessageStartingWith("Не найден пользователь")
                .asInstanceOf(InstanceOfAssertFactories.type(NotFoundException.class));

    }

    @Test
    void updateUser() {
        User user = UserTestData.getUserOne();
        String name = "NameUserOne";
        String email = "dodo@example.com";
        user.setName(name);
        user.setEmail(email);
        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        User updated = userService.update(user.getId(), user);

        Assertions.assertThat(updated).isNotNull();
        Assertions.assertThat(updated.getName()).isEqualTo(name);
        Assertions.assertThat(updated.getEmail()).isEqualTo(email);
    }

    @Test
    public void remove() {
        Long userId = 1L;
        userService.remove(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    public void testGetAll() {
        List<User> userList = new ArrayList<>();
        User userOne = UserTestData.getUserOne();
        User userTwo = UserTestData.getUserTwo();
        userList.add(userOne);
        userList.add(userTwo);
        when(userRepository.findAll()).thenReturn(userList);
        userList = userService.getAllUsers();

        Assertions.assertThat(userList.size()).isEqualTo(2);
    }

    @Test
    public void testUpdateName() {
        User user = UserTestData.getUserOne();
        String name = "UserNameOne";
        user.setName(name);
        when(userRepository.findById(user.getId())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        User updated = userService.update(user.getId(), user);

        Assertions.assertThat(updated.getName()).isEqualTo(name);
    }


}
