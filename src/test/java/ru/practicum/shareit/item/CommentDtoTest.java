package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.parse("2023-08-04T13:26:59");
        CommentDto commentDto = new CommentDto(1L,
                "Отличная вещь",
                "Константин",
                localDateTime);

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Константин");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-08-04T13:26:59");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь");
    }
}
