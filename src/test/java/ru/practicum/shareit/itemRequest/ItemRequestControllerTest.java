package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.testData.ItemRequestTestData;
import ru.practicum.shareit.testData.ItemTestData;
import ru.practicum.shareit.testData.UserTestData;
import ru.practicum.shareit.user.Service.UserService;
import ru.practicum.shareit.user.model.User;

import static org.mockito.Mockito.when;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;
    @MockBean
    UserService userService;
    @MockBean
    ItemRequestService itemRequestService;

//    @Test
//    @SneakyThrows
//    public void createItemRequest() {
//        User user = UserTestData.getUserTwo();
//        User owner = UserTestData.getUserOne();
//        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
//        Item item = ItemTestData.getItemOne(itemRequest, owner);
//        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
//
//        when(itemRequestService.createItemRequest(itemRequest)).thenReturn(itemRequest);
//
//        MvcResult result = mockMvc.perform(
//                        MockMvcRequestBuilders.post("/requests")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("X-Sharer-User-Id", owner.getId())
//                                .content(objectMapper.writeValueAsString(itemRequestDto))
//                )
//                .andDo(print())
//                .andReturn();
//        String resultItemRequest = result.getResponse().getContentAsString();
//        ItemRequestResponseDto itemRequestResponseDto = objectMapper.readValue(resultItemRequest, ItemRequestResponseDto.class);
//
//        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
//        Assertions.assertThat(itemRequestResponseDto.getId()).isEqualTo(item.getId());
//
//
//    }


}
