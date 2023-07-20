package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapperTest {

    @Test
    public void testMapDtoToUser() {
        Long id = 1L;
        String name = "User One";
        String email = "userrr@example.com";

        UserDto userDto = new UserDto(id, name, email);

        User user = UserMapper.toUser(userDto);

        Assertions.assertThat(id).isEqualTo(user.getId());
        Assertions.assertThat(name).isEqualTo(user.getName());
        Assertions.assertThat(email).isEqualTo(user.getEmail());
    }

    @Test
    public void testMapUserToDto() {
        Long id = 1L;
        String name = "User One";
        String email = "userrr@example.com";

        User user = new User(id, name, email);

        UserDto userDto = UserMapper.toUserDto(user);

        Assertions.assertThat(id).isEqualTo(userDto.getId());
        Assertions.assertThat(name).isEqualTo(userDto.getName());
        Assertions.assertThat(email).isEqualTo(userDto.getEmail());
    }

    @Test
    public void testToUserDtoList() {
        List<User> userList = new ArrayList<>();
        User userOne = new User(3L, "Name", "email@example.com");
        User userTwo = new User(3L, "User", "email12@example.com");
        userList.add(userOne);
        userList.add(userTwo);

        List<UserDto> userDtos = UserMapper.toUserDtoList(userList);

        Assertions.assertThat(userDtos.size()).isEqualTo(2);
    }
}
