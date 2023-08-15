package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
public class ItemRequestDtoTest {
    @Autowired
   private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.parse("2023-08-04T13:26:59");

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "Нужен стол с одной ножкой",
                1L,
                localDateTime
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужен стол с одной ножкой");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-08-04T13:26:59");
    }


}
