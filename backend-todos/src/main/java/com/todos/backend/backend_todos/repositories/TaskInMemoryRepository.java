package com.todos.backend.backend_todos.repositories;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;

/**
 * Repository for Task entity using in-memory storage
 */
@Repository
@Primary
@Profile("in-memory")
public class TaskInMemoryRepository  implements TaskRepository{
    // In-memory storage
    private final Map<UUID, Task> database = new HashMap<>();

    /**
     * Save a task in the in-memory storage
     * @param task Task to save
     */
    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID());
        }
        database.put(task.getId(), task);

        return task;
    }

    /**
     * Find a task by its ID
     * @param id ID of the task to find
     * @return Task with the given ID or null if not found
     */
    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    /**
     * Delete a task from the in-memory storage
     * @param task Task to delete
     */
    @Override
    public void delete(Task task) {
        database.remove(task.getId());
    }

    /**
     * Find tasks by done, text and priority
     * @param done Task done status to search
     * @param text Task task description to search
     * @param priority Task priority to search
     * @param pageable Pageable object to paginate the results
     * @return Page of tasks that match the search criteria
     */
    @Override
    public Page<Task> findByDoneTextAndPriority(Boolean done, String text, Priority priority, Pageable pageable) {
        List<Task> filteredList = database.values().stream()
            .filter(task -> (done == null || task.getDone().equals(done)) &&
                            (text == null || task.getText().toLowerCase().contains(text.toLowerCase())) && 
                            (priority == null || task.getPriority() == priority))
            .sorted(createComparatorFromSort(pageable.getSort()))
            .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredList.size());
            List<Task> paginatedList = filteredList.subList(start, end);

            return new PageImpl<>(paginatedList, pageable, filteredList.size());
    }

    /**
     * Create a comparator from a Sort object
     * @param sort Sort object to create the comparator from
     * @return Comparator object
     */
    private Comparator<Task> createComparatorFromSort(Sort sort) {
        Comparator<Task> comparator = null;
    
        for (Sort.Order order : sort) {
            Comparator<Task> fieldComparator;
    
            switch (order.getProperty()) {
                case "dueDate":
                    fieldComparator = Comparator.comparing(Task::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder()));
                    break;
                case "priority":
                    fieldComparator = Comparator.comparing(Task::getPriority);
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
    
        return comparator == null ? Comparator.comparing(Task::getCreationDate) : comparator; // Default sort
    }
    
}
