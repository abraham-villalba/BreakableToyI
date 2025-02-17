package com.todos.backend.backend_todos.services;

import java.time.LocalDate;
import java.time.ZoneId;
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

import com.todos.backend.backend_todos.dto.NewTask;
import com.todos.backend.backend_todos.dto.TaskStatistics;
import com.todos.backend.backend_todos.exceptions.TaskNotFoundException;
import com.todos.backend.backend_todos.models.Task;
import com.todos.backend.backend_todos.repositories.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;


    private static final Set<String> VALID_FIELDS = Set.of("priority", "dueDate");
    private static final Set<String> VALID_ORDERS = Set.of("asc", "desc");

    public TaskService() {
    }

    public Task createTask(NewTask task) {
        Task newTask = new Task();
        if (task.getDueDate() != null) {
            LocalDate today = LocalDate.now(); // Current date without time
            LocalDate dueDate = task.getDueDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
            if (dueDate.isBefore(today)) {
                throw new IllegalArgumentException("Due date cannot be in the past.");
            }
        }
        newTask.setCreationDate(new Date());
        newTask.setDone(false);
        newTask.setDueDate(task.getDueDate());
        
        newTask.setText(task.getText());
        newTask.setPriority(task.getPriority());
        return repository.save(newTask);
    }

    public Task updateTask(UUID id, NewTask updatedTask) {
        Optional<Task> currentTask = repository.findById(id);
        // Task does not exist
        if(currentTask.isEmpty()) {
            throw new TaskNotFoundException("To Do not found with id " + id);
        } 
        // Update the currentTask
        Task task = currentTask.get();
        if (updatedTask.getDueDate() != null) {
            LocalDate creationDate = task.getCreationDate().toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
            LocalDate dueDate = updatedTask.getDueDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
            if (dueDate.isBefore(creationDate)) {
                throw new IllegalArgumentException("Due date cannot be in the past.");
            }
        }
        task.setDueDate(updatedTask.getDueDate());
        task.setText(updatedTask.getText());
        task.setPriority(updatedTask.getPriority());
        return repository.save(task);
    }

    public Task completeTask(UUID id) {
        Optional<Task> currentTask = repository.findById(id);
        // Task does not exist
        if(currentTask.isEmpty()) {
            throw new TaskNotFoundException("To Do not found with id " + id);
        } 
        // Update the currentTask
        Task task = currentTask.get();
        task.setDone(true);
        if (task.getDoneDate() == null) {
            task.setDoneDate(new Date());
        }
        return repository.save(task);
    }

    public Task uncompleteTask(UUID id) {
        Optional<Task> currentTask = repository.findById(id);
        // Task does not exist
        if(currentTask.isEmpty()) {
            throw new TaskNotFoundException("To Do not found with id " + id);
        } 
        // Update the currentTask
        Task task = currentTask.get();
        // TODO: I think this will require further validation.
        task.setDone(false);
        if (task.getDoneDate() != null) {
            task.setDoneDate(null);
        }
        return repository.save(task);
    }

    public void deleteTask(UUID id) {
        Optional<Task> currentTask = repository.findById(id);
        // Task does not exist
        if(currentTask.isEmpty()) {
            throw new TaskNotFoundException("To Do not found with id " + id);
        } 
        // Update the currentTask
        Task task = currentTask.get();
    
        repository.delete(task);
    }

    public Page<Task> getAllTasksFilterAndSort(
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

    public TaskStatistics geTaskStatistics() {
        long startTime = System.currentTimeMillis();
        TaskStatistics stats = new TaskStatistics();
        Long totalDoneSeconds = 0L;
        Long totalLowDoneSeconds = 0L;
        Long totalMediumDoneSeconds = 0L;
        Long totalHighDoneSeconds = 0L;
        int currentPage = 0;

        Pageable pageable = PageRequest.of(currentPage, 100);
        Page<Task> page;

        boolean hasNext = true;
        while (hasNext) {
            page = repository.findByDoneTextAndPriority(true, null, null, pageable);
            for (Task task : page) {
                if (task.getDoneDate() == null || task.getCreationDate() == null) {
                    continue;
                }
                
                stats.incrementTotalDone();
                Long elapsedTimeSeconds = (task.getDoneDate().getTime() - task.getCreationDate().getTime()) / 1000;
                totalDoneSeconds += elapsedTimeSeconds;
                switch (task.getPriority()) {
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
