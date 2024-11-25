package com.todos.backend.backend_todos.repositories;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.ToDo;

@Repository
@Primary
public class ToDoInMemoryRepository  implements ToDoRepository{
    private final Map<UUID, ToDo> database = new HashMap<>();

    @Override
    public ToDo save(ToDo toDo) {
        if (toDo.getId() == null) {
            toDo.setId(UUID.randomUUID());
        }
        database.put(toDo.getId(), toDo);

        return toDo;
    }

    @Override
    public Optional<ToDo> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public void delete(ToDo toDo) {
        database.remove(toDo.getId());
    }

    @Override
    public Page<ToDo> findByDoneTextAndPriority(Boolean done, String text, Priority priority, Pageable pageable) {
        List<ToDo> filteredList = database.values().stream()
            .filter(todo -> (done == null || todo.getDone().equals(done)) &&
                            (text == null || todo.getText().toLowerCase().contains(text.toLowerCase())) && 
                            (priority == null || todo.getPriority() == priority))
            .sorted(createComparatorFromSort(pageable.getSort()))
            .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredList.size());
            List<ToDo> paginatedList = filteredList.subList(start, end);

            return new PageImpl<>(paginatedList, pageable, filteredList.size());
    }

    private Comparator<ToDo> createComparatorFromSort(Sort sort) {
        Comparator<ToDo> comparator = null;
    
        for (Sort.Order order : sort) {
            Comparator<ToDo> fieldComparator;
    
            switch (order.getProperty()) {
                case "dueDate":
                    fieldComparator = Comparator.comparing(ToDo::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder()));
                    break;
                case "priority":
                    fieldComparator = Comparator.comparing(ToDo::getPriority);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sort field: " + order.getProperty());
            }
    
            // Apply ascending/descending order
            if (order.isDescending()) {
                fieldComparator = fieldComparator.reversed();
            }
    
            // Chain comparators
            comparator = (comparator == null) ? fieldComparator : comparator.thenComparing(fieldComparator);
        }
    
        return comparator == null ? Comparator.comparing(ToDo::getCreationDate) : comparator; // Default sort
    }
    
}
