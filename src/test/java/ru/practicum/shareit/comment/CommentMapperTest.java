package ru.practicum.shareit.comment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


public class CommentMapperTest {
    @Test
    public void testToComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("New Comment");
        commentDto.setCreated(LocalDateTime.now());

        Comment comment = CommentMapper.toComment(commentDto);

        Assertions.assertThat(commentDto.getId()).isEqualTo(comment.getId());
        Assertions.assertThat(commentDto.getText()).isEqualTo(comment.getText());
        Assertions.assertThat(commentDto.getCreated()).isEqualTo(comment.getCreated());
    }

    @Test
    public void testToDto() {
        Comment comment = new Comment();
        User user = new User(3L, "Name", "email@example.com");
        comment.setId(2L);
        comment.setText("Another comment");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        Assertions.assertThat(comment.getId()).isEqualTo(commentDto.getId());
        Assertions.assertThat(comment.getText()).isEqualTo(commentDto.getText());
        Assertions.assertThat(comment.getAuthor().getName()).isEqualTo(commentDto.getAuthorName());
        Assertions.assertThat(comment.getCreated()).isEqualTo(commentDto.getCreated());
    } @Test
    public void testToDtoNull() {
        CommentDto commentDto = CommentMapper.toCommentDto(null);

        Assertions.assertThat(commentDto).isNull();
    }
}
