package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

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
        return itemClient.getAll(userId);
    }

    @GetMapping({"/{itemId}"})
    public ResponseEntity<Object> getItemByIdr(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                          @PathVariable("itemId") Long itemId) {
        log.debug("Item with Id {} requested", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.debug("Create item requested");
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                         @PathVariable("itemId") Long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.debug("Update item with id {} requested", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable Long itemId) {
        log.debug("Delete item with id {} requested", itemId);
        itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByParams(
            @RequestParam String text
    ) {
        log.info("Processing method findItemByParams with params: text = {}", text);
        return itemClient.searchForItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Processing method postComment with params: itemId= {}, userId = {}, commentDto = {}", itemId, userId, commentDto);
        return itemClient.addNewComment(userId, itemId, commentDto);
    }

}