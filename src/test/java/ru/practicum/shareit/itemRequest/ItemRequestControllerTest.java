package ru.practicum.shareit.itemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.Service.UserService;

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
