package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    public void testCreateUser() {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = UserTestData.getUserOne();
        UserDto userDto = UserMapper.toUserDto(user);

        when(userService.createUser(user)).thenReturn(user);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andDo(print())
                .andReturn();

        String resultUserStr = result.getResponse().getContentAsString();
        UserDto resultUserDto = objectMapper.readValue(resultUserStr, UserDto.class);

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        Assertions.assertThat(resultUserDto.getId()).isEqualTo(user.getId());
        Assertions.assertThat(resultUserDto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(resultUserDto.getName()).isEqualTo(user.getName());
        Assertions.assertThat(resultUserDto).isNotNull();
    }

    @Test
    @SneakyThrows
    public void testGetUserById() {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = UserTestData.getUserOne();
        UserDto userDto = UserMapper.toUserDto(user);
        Long userId = 1l;

        when(userService.findById(userId)).thenReturn(user);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andDo(print())
                .andReturn();

        String resultUserStr = result.getResponse().getContentAsString();
        UserDto resultUserDto = objectMapper.readValue(resultUserStr, UserDto.class);

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        Assertions.assertThat(resultUserDto.getId()).isEqualTo(user.getId());
        Assertions.assertThat(resultUserDto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(resultUserDto.getName()).isEqualTo(user.getName());
        Assertions.assertThat(resultUserDto).isNotNull();
    }

    @Test
    @SneakyThrows
    public void testUpdateUser() {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = UserTestData.getUserOne();
        when(userService.update(eq(user.getId()), any(User.class))).thenReturn(user);
        String name = "NameUserOne";
        String email = "dodo@example.com";
        user.setName(name);
        user.setEmail(email);
        UserDto userDto = UserMapper.toUserDto(user);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.patch("/users/{userId}", user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                )
                .andDo(print())
                .andReturn();

        String resultUserStr = result.getResponse().getContentAsString();
        UserDto resultUserDto = objectMapper.readValue(resultUserStr, UserDto.class);

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        Assertions.assertThat(resultUserDto.getId()).isEqualTo(user.getId());
        Assertions.assertThat(resultUserDto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(resultUserDto.getName()).isEqualTo(user.getName());
        Assertions.assertThat(resultUserDto).isNotNull();
    }

    @Test
    @SneakyThrows
    public void testDeleteUserById() {
        Long userId = 1l;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.delete("/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();

    }

    @Test
    @SneakyThrows
    public void testFindAllUser() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> userList = new ArrayList<>();
        User user = UserTestData.getUserOne();
        User user1 = UserTestData.getUserTwo();
        userList.add(user);
        userList.add(user1);
        List<UserDto> userDtoList = UserMapper.toUserDtoList(userList);
        when(userService.getAllUsers()).thenReturn(userList);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDtoList))
                )
                .andDo(print())
                .andReturn();

        String resultUserStr = result.getResponse().getContentAsString();
        List<UserDto> userDtos = objectMapper.readValue(resultUserStr, new TypeReference<List<UserDto>>() {
        });

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(userDtos.size()).isEqualTo(2);

    }
}
