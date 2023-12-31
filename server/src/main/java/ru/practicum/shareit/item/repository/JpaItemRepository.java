package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface JpaItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')) AND i.available = true")
    List<Item> getSearch(String text);

    List<Item> findAllByOwnerIdOrderById(Long userId);

    List<Item> findAllByRequestId(Long requestId);

}
