package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.CommentDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllItems(Long userId) {
        return get("/", userId);
    }


    public ResponseEntity<Object> create(long userId, ItemDto itemDto) {
        return post("/", userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(long userId, Long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return get("/" + itemId, userId, parameters);
    }

    public ResponseEntity<Object> updateItem(long userId, Long itemId, UpdateItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> deleteItem(long userId, Long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return delete("/" + itemId, userId, parameters);
    }

    public ResponseEntity<Object> findItemByParams(String text) {
        return get(String.format("/search?text=%s", text));
    }

    public ResponseEntity<Object> postComment(long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
