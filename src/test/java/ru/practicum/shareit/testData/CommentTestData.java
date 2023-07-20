package ru.practicum.shareit.testData;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentTestData {
    public static Comment getCommentTwo(User userComment, Item item) {
         Comment commentTwo = new Comment(2L, "Благодарю владельца вещи. Всё отлично", item, userComment, LocalDateTime.now());
         return commentTwo;
     }

    public static Comment getCommentOne(User userComment, Item item) {
         Comment commentOne = new Comment(1L, "Хорошая вещь, спасибо", item, userComment, LocalDateTime.now().minusMinutes(20));
         return commentOne;
     }
}
