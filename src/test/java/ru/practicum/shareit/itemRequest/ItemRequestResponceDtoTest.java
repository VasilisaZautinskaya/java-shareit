package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestResponceDtoTest {
    @Autowired
    JacksonTester<ItemRequestResponseDto> json;

    @Test
    void testItemRequestResponceDto() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.parse("2023-08-04T13:26:59");

        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(1L,
                "Нужен стол с одной ножкой",
                1L,
                new ArrayList<>(),
                localDateTime);

        JsonContent<ItemRequestResponseDto> result = json.write(itemRequestResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужен стол с одной ножкой");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-08-04T13:26:59");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);
    }
}
