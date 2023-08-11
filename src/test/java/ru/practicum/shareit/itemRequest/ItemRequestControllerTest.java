package ru.practicum.shareit.itemRequest;

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
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemRequestService itemRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    public void testCreateItemRequest() {
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);

        when(itemRequestService.createItemRequest(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHARER_USER_ID, user.getId())
                                .content(objectMapper.writeValueAsString(itemRequestDto))
                )
                .andDo(print())
                .andReturn();

        String resultItemRequest = result.getResponse().getContentAsString();
        ItemRequestResponseDto itemRequestResponseDto = objectMapper.readValue(resultItemRequest, ItemRequestResponseDto.class);

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(itemRequestResponseDto.getId()).isEqualTo(itemRequest.getId());

    }

    @Test
    @SneakyThrows
    public void testFindItemRequest() {
        List<Item> itemList = new ArrayList<>();
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        itemList.add(item);
        when(itemRequestService.findById(itemRequest.getId(), user.getId())).thenReturn(itemRequest);

        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest, itemList);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/requests/{requestId}", itemRequest.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHARER_USER_ID, user.getId())
                                .content(objectMapper.writeValueAsString(itemRequestResponseDto))
                )
                .andDo(print())
                .andReturn();

        String resultItemRequest = result.getResponse().getContentAsString();
        ItemRequestResponseDto itemRequestResponseDto2 = objectMapper.readValue(resultItemRequest, ItemRequestResponseDto.class);

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(itemRequestResponseDto2.getId()).isEqualTo(itemRequest.getId());
        Assertions.assertThat(itemRequestResponseDto2.getRequestorId()).isEqualTo(itemRequest.getRequestor().getId());


    }

    @Test
    @SneakyThrows
    public void testGetAllItemRequests() {
        List<Item> itemList = new ArrayList<>();
        List<ItemRequest> itemRequests = new ArrayList<>();
        List<ItemRequestResponseDto> itemRequestResponseDtoList = new ArrayList<>();
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        itemRequestResponseDtoList.add(ItemRequestMapper.toItemRequestResponseDto(itemRequest, itemList));
        itemList.add(item);
        itemRequests.add(itemRequest);
        int from = 0;
        int size = 10;
        when(itemRequestService.findAllItemRequests(user.getId(), from, size)).thenReturn(itemRequests);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/requests/all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHARER_USER_ID, user.getId())
                                .param("from", String.valueOf(from))
                                .param("size", String.valueOf(size))
                                .content(objectMapper.writeValueAsString(itemRequestResponseDtoList))
                )
                .andDo(print())
                .andReturn();

        String resultItemRequest = result.getResponse().getContentAsString();
        List<ItemRequestResponseDto> itemRequestResponseDtoList1 = objectMapper.readValue(resultItemRequest,
                new TypeReference<List<ItemRequestResponseDto>>() {
                });
        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(itemRequestResponseDtoList1.size()).isEqualTo(itemRequestResponseDtoList.size());

    }

    @Test
    @SneakyThrows
    public void testFindItemRequestById() {
        List<Item> itemList = new ArrayList<>();
        List<ItemRequest> itemRequests = new ArrayList<>();
        List<ItemRequestResponseDto> itemRequestResponseDtoList = new ArrayList<>();
        User user = UserTestData.getUserTwo();
        User owner = UserTestData.getUserOne();
        ItemRequest itemRequest = ItemRequestTestData.getItemRequest(user);
        Item item = ItemTestData.getItemOne(itemRequest, owner);
        itemRequestResponseDtoList.add(ItemRequestMapper.toItemRequestResponseDto(itemRequest, itemList));
        itemList.add(item);
        itemRequests.add(itemRequest);
        when(itemRequestService.findAllItemRequest(user.getId())).thenReturn(itemRequests);

        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(itemRequest, itemList);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHARER_USER_ID, user.getId())
                                .content(objectMapper.writeValueAsString(itemRequestResponseDto))
                )
                .andDo(print())
                .andReturn();

        String resultItemRequest = result.getResponse().getContentAsString();
        List<ItemRequestResponseDto> itemRequestResponseDtoList1 = objectMapper.readValue(resultItemRequest,
                new TypeReference<List<ItemRequestResponseDto>>() {
                });
        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        Assertions.assertThat(itemRequestResponseDtoList1.size()).isEqualTo(itemRequestResponseDtoList.size());


    }

}