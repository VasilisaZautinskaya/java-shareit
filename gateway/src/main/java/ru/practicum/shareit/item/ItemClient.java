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

    public ResponseEntity<Object> getAll(Long userId) {
        return get("/", userId);
    }


    public ResponseEntity<Object> addItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return get("/" + itemId, userId, parameters);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return patch("/" + itemId, userId, parameters, itemDto);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return delete("/" + itemId, userId, parameters);
    }

    public ResponseEntity<Object> searchForItems(String text) {
        return get("/" + text);
    }

    public ResponseEntity<Object> addNewComment(Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
