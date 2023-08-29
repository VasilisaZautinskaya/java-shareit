package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(
            @NotNull @RequestHeader(X_SHARER_USER_ID) Long userId,
            @NotNull @RequestBody @Valid ItemRequestDto itemRequestDto
    ) {
        ResponseEntity<Object> itemRequestDtoCreated = itemRequestClient.addItemRequest(userId, itemRequestDto);
        log.info("Processing method create with params: userId = {}, itemRequestDto = {}", userId, itemRequestDto);
        return itemRequestDtoCreated;
    }

    @GetMapping
    public ResponseEntity<Object> getById(@NotNull @RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Processing method getById with params: userId = {}", userId);
        ResponseEntity<Object> itemRequestDtoCreated = itemRequestClient.getOwnItemRequests(userId);
        return itemRequestDtoCreated;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @NotNull @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Processing method getAllItemRequests with params: userId = {},  from = {}, size = {}", userId, from, size);
        ResponseEntity<Object> itemRequestDtoCreated = itemRequestClient.getAll(userId, from, size);
        return itemRequestDtoCreated;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @NotNull @RequestHeader(X_SHARER_USER_ID) Long userId,
            @NotNull @PathVariable("requestId") Long requestId
    ) {
        log.info("Processing method findById with params: userId = {}, requestId = {}", userId, requestId);
        ResponseEntity<Object> itemRequestDtoCreated = itemRequestClient.findById(userId, requestId);
        return itemRequestDtoCreated;
    }
}
