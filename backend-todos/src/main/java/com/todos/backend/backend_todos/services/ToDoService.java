package com.todos.backend.backend_todos.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.List;

import com.todos.backend.backend_todos.models.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.dto.ToDoStatistics;
import com.todos.backend.backend_todos.exceptions.ToDoNotFoundException;
import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.repositories.ToDoRepository;

@Service
public class ToDoService {

    @Autowired
    private ToDoRepository repository;

    private static final Set<String> VALID_FIELDS = Set.of("priority", "dueDate");
    private static final Set<String> VALID_ORDERS = Set.of("asc", "desc");

    public ToDoService() {
    }

    public ToDo createToDo(NewToDo toDo) {
        ToDo newToDo = new ToDo();
        newToDo.setCreationDate(new Date());
        newToDo.setDone(false);
        newToDo.setDueDate(toDo.getDueDate());
        newToDo.setText(toDo.getText());
        newToDo.setPriority(toDo.getPriority());
        return repository.save(newToDo);
    }

    public ToDo updateToDo(UUID id, NewToDo updatedToDo) {
        Optional<ToDo> currentToDo = repository.findById(id);
        // ToDo does not exist
        if(currentToDo.isEmpty()) {
            throw new ToDoNotFoundException("To Do not found with id " + id);
        } 
        // Update the currentToDo
        ToDo toDo = currentToDo.get();
        // TODO: I think this will require further validation.
        toDo.setDueDate(updatedToDo.getDueDate());
        toDo.setText(updatedToDo.getText());
        toDo.setPriority(updatedToDo.getPriority());
        return repository.save(toDo);
    }

    public ToDo completeToDo(UUID id) {
        Optional<ToDo> currentToDo = repository.findById(id);
        // ToDo does not exist
        if(currentToDo.isEmpty()) {
            throw new ToDoNotFoundException("To Do not found with id " + id);
        } 
        // Update the currentToDo
        ToDo toDo = currentToDo.get();
        // TODO: I think this will require further validation.
        toDo.setDone(true);
        if (toDo.getDoneDate() == null) {
            toDo.setDoneDate(new Date());
        }
        return repository.save(toDo);
    }

    public ToDo uncompleteToDo(UUID id) {
        Optional<ToDo> currentToDo = repository.findById(id);
        // ToDo does not exist
        if(currentToDo.isEmpty()) {
            throw new ToDoNotFoundException("To Do not found with id " + id);
        } 
        // Update the currentToDo
        ToDo toDo = currentToDo.get();
        // TODO: I think this will require further validation.
        toDo.setDone(false);
        if (toDo.getDoneDate() != null) {
            toDo.setDoneDate(null);
        }
        return repository.save(toDo);
    }

    public List<ToDo> getAllToDos() {
        return repository.findAll();
    }

    public void deleteToDo(UUID id) {
        Optional<ToDo> currentToDo = repository.findById(id);
        // ToDo does not exist
        if(currentToDo.isEmpty()) {
            throw new ToDoNotFoundException("To Do not found with id " + id);
        } 
        // Update the currentToDo
        ToDo toDo = currentToDo.get();
    
        repository.delete(toDo);
    }

    public Page<ToDo> getAllToDosFilterAndSort(
        Integer page,
        Integer size,
        Boolean doneFilter, 
        String textFilter, 
        Priority priorityFilter,
        String sortList 
    ) {
        Sort sort = parseSortParameter(sortList);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findByDoneTextAndPriority(doneFilter, textFilter, priorityFilter, pageable);
    }

    public ToDoStatistics geToDoStatistics() {
        long startTime = System.currentTimeMillis();
        ToDoStatistics stats = new ToDoStatistics();
        Long totalDoneSeconds = 0L;
        Long totalLowDoneSeconds = 0L;
        Long totalMediumDoneSeconds = 0L;
        Long totalHighDoneSeconds = 0L;
        int currentPage = 0;

        Pageable pageable = PageRequest.of(currentPage, 100);
        Page<ToDo> page;

        boolean hasNext = true;
        while (hasNext) {
            page = repository.findByDoneTextAndPriority(true, null, null, pageable);
            for (ToDo toDo : page) {
                if (toDo.getDoneDate() == null || toDo.getCreationDate() == null) {
                    continue;
                }
                
                stats.incrementTotalDone();
                Long elapsedTimeSeconds = (toDo.getDoneDate().getTime() - toDo.getCreationDate().getTime()) / 1000;
                totalDoneSeconds += elapsedTimeSeconds;
                switch (toDo.getPriority()) {
                    case Priority.LOW:
                        stats.incrementLowDone();
                        totalLowDoneSeconds += elapsedTimeSeconds;
                        break;
                    case Priority.MEDIUM:
                        stats.incrementMediumDone();
                        totalMediumDoneSeconds += elapsedTimeSeconds;
                        break;
                    default:
                        stats.incrementHighDone();
                        totalHighDoneSeconds += elapsedTimeSeconds;
                        break;
                }
            }
            hasNext = page.hasNext();
            currentPage++;
            pageable = PageRequest.of(currentPage, 100);
        }

        Long avgTime = 0L;

        if (stats.getTotalDone() > 0) {
            avgTime = totalDoneSeconds / stats.getTotalDone();
            stats.setAverageDoneTime(formatAverageTime(avgTime));
        }
        if (stats.getTotalLowDone() > 0) {
            avgTime = totalLowDoneSeconds / stats.getTotalLowDone();
            stats.setAverageLowDoneTime(formatAverageTime(avgTime));
        }
        if (stats.getTotalHighDone() > 0) {
            avgTime = totalHighDoneSeconds / stats.getTotalHighDone();
            stats.setAverageHighDoneTime(formatAverageTime(avgTime));
        }
        if (stats.getTotalMediumDone() > 0) {
            avgTime = totalMediumDoneSeconds / stats.getTotalMediumDone();
            stats.setAverageMediumDoneTime(formatAverageTime(avgTime));
        }

        // End time
        long endTime = System.currentTimeMillis();

        // Calculate elapsed time in milliseconds
        long elapsedTime = endTime - startTime;

        // Print the elapsed time
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");

        return stats;
    }

    private Sort parseSortParameter(String sortList) {
        if (sortList == null || sortList.isBlank()) {
            System.out.println("Lista esta vacia");
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();
        String[] sortFields = sortList.split(",");

        for (String s : sortFields) {
            String[] fieldAndOrder = s.split(":");
            if (fieldAndOrder.length != 2) {
                throw new IllegalArgumentException("Invalid sort field: " + fieldAndOrder);
            }
            String field = fieldAndOrder[0].trim();
            String order = fieldAndOrder[1].trim();

            if (!VALID_FIELDS.contains(field)) {
                throw new IllegalArgumentException("Invalid sort field: " + field);
            }

            if (!VALID_ORDERS.contains(order)) {
                throw new IllegalArgumentException("Invalid sort field: " + order);
            }

            Sort.Order sortOrder = order.equals("asc") ? Sort.Order.asc(field) : Sort.Order.desc(field);

            orders.add(sortOrder);
        }

        return Sort.by(orders);
        
    }

    private String formatAverageTime(Long averageTimeSeconds) {
        System.out.println("Time in seconds: " + averageTimeSeconds);
        long minutes = averageTimeSeconds / 60;
        // long hours = 0;
        // if (minutes >= 60) {
        //     hours = minutes / 60;
        //     minutes = minutes % 60;
        // }
        //System.out.println("Time in hours: " + hours);
        long seconds = averageTimeSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
}
