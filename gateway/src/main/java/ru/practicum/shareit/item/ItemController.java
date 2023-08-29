package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(X_SHARER_USER_ID) Long userId
    ) {
        log.info("Processing method getAllItems with params: userId = {}", userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping({"/{itemId}"})
    public ResponseEntity<Object> getItemById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                              @PathVariable("itemId") Long itemId) {
        log.info("Processing method getItemById with params: userId = {}, itemId = {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(X_SHARER_USER_ID) long userId
    ) {
        log.info("Processing method create with params: userId = {}, itemDto = {}", userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @Valid @RequestBody UpdateItemDto itemDto) {
        log.debug("Update item with id {} requested", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(X_SHARER_USER_ID) long userId, @PathVariable Long itemId) {
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByParams(
            @RequestParam String text
    ) {
        log.info("Processing method findItemByParams with params: text = {}", text);
        return itemClient.findItemByParams(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Processing method postComment with params: itemId= {}, userId = {}, commentDto = {}", itemId, userId, commentDto);
        return itemClient.postComment(userId, itemId, commentDto);
    }

}