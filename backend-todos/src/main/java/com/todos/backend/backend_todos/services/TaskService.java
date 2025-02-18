package com.todos.backend.backend_todos.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.List;

import com.todos.backend.backend_todos.models.Priority;
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

/**
 * Service class for handling tasks and their statistics.
 */
@Service
public class TaskService {

    private final TaskRepository repository;

    // Valid fields and order for sorting
    private static final Set<String> VALID_FIELDS = Set.of("priority", "dueDate");
    private static final Set<String> VALID_ORDERS = Set.of("asc", "desc");

    /**
     * Constructs a new TaskService with the given TaskRepository.
     *
     * @param repository the task repository to use
     */
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new task.
     *
     * @param task the task to create
     * @return the created task
     * @throws IllegalArgumentException if the due date is in the past
     */
    public Task createTask(NewTask task) {
        Task newTask = new Task();
        if (task.getDueDate() != null) {
            LocalDate today = LocalDate.now(); // Current date without time
            LocalDate dueDate = task.getDueDate();
            if (dueDate.isBefore(today)) {
                throw new IllegalArgumentException("Due date cannot be in the past.");
            }
        }
        newTask.setCreationDate(Instant.now());
        newTask.setDone(false);
        newTask.setDueDate(task.getDueDate() != null ? task.getDueDate().atStartOfDay(ZoneOffset.UTC).toInstant() : null);
        
        newTask.setText(task.getText());
        newTask.setPriority(task.getPriority());
        return repository.save(newTask);
    }

    /**
     * Updates an existing task.
     *
     * @param id the ID of the task to update
     * @param updatedTask the updated task details
     * @return the updated task
     * @throws TaskNotFoundException if the task with the specified ID is not found
     * @throws IllegalArgumentException if the due date is in the past
     */
    public Task updateTask(UUID id, NewTask updatedTask) {
        Optional<Task> currentTask = repository.findById(id);
        // Task does not exist
        if(currentTask.isEmpty()) {
            throw new TaskNotFoundException("Task not found with id " + id);
        } 
        // Update the currentTask
        Task task = currentTask.get();
        if (updatedTask.getDueDate() != null) {
            LocalDate creationDate = task.getCreationDate()
                                            .atZone(ZoneOffset.UTC)
                                            .toLocalDate();
            LocalDate dueDate = updatedTask.getDueDate();
            if (dueDate.isBefore(creationDate)) {
                throw new IllegalArgumentException("Due date cannot be in the past.");
            }
        }
        task.setDueDate(updatedTask.getDueDate() != null ? updatedTask.getDueDate().atStartOfDay(ZoneOffset.UTC).toInstant() : null);
        task.setText(updatedTask.getText());
        task.setPriority(updatedTask.getPriority());
        return repository.save(task);
    }

    /**
     * Marks a task as done.
     *
     * @param id the ID of the task to mark as done
     * @return the task that was marked as done
     * @throws TaskNotFoundException if the task with the specified ID is not found
     */
    public Task completeTask(UUID id) {
        Optional<Task> currentTask = repository.findById(id);
        // Task does not exist
        if(currentTask.isEmpty()) {
            throw new TaskNotFoundException("Task not found with id " + id);
        } 
        // Update the currentTask
        Task task = currentTask.get();
        task.setDone(true);
        if (task.getDoneDate() == null) {
            task.setDoneDate(Instant.now());
        }
        return repository.save(task);
    }

    /**
     * Marks a task as not done.
     *
     * @param id the ID of the task to mark as not done
     * @return the task that was marked as not done
     * @throws TaskNotFoundException if the task with the specified ID is not found
     */
    public Task uncompleteTask(UUID id) {
        Optional<Task> currentTask = repository.findById(id);
        // Task does not exist
        if(currentTask.isEmpty()) {
            throw new TaskNotFoundException("Task not found with id " + id);
        } 
        // Update the currentTask
        Task task = currentTask.get();
        task.setDone(false);
        if (task.getDoneDate() != null) {
            task.setDoneDate(null);
        }
        return repository.save(task);
    }

    /**
     * Deletes a task.
     *
     * @param id the ID of the task to delete
     * @throws TaskNotFoundException if the task with the specified ID is not found
     */
    public void deleteTask(UUID id) {
        Optional<Task> currentTask = repository.findById(id);
        // Task does not exist
        if(currentTask.isEmpty()) {
            throw new TaskNotFoundException("Task not found with id " + id);
        } 
        // Update the currentTask
        Task task = currentTask.get();
    
        repository.delete(task);
    }

    /**
     * Retrieves all tasks, optionally filtered and sorted.
     *
     * @param page the page number to retrieve
     * @param size the number of tasks per page
     * @param doneFilter the done status to filter by
     * @param textFilter the text to filter by
     * @param priorityFilter the priority to filter by
     * @param sortList the field to sort by
     * @return a page of tasks
     */
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

    /**
     * Retrieves statistics about tasks.
     *
     * @return the task statistics
     */
    public TaskStatistics getTaskStatistics() {
        long startTime = System.currentTimeMillis();
        TaskStatistics stats = new TaskStatistics();
        int currentPage = 0;
        Pageable pageable = PageRequest.of(currentPage, 100);
        boolean hasNext = true;

        while (hasNext) {
            Page<Task> page = repository.findByDoneTextAndPriority(true, null, null, pageable);
            processTasks(page, stats);
            hasNext = page.hasNext();
            currentPage++;
            pageable = PageRequest.of(currentPage, 100);
        }
    
        calculateAndSetAverages(stats);
    
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
    
        return stats;
    }

    /**
     * Processes the tasks in the given page and updates the statistics.
     *
     * @param page the page of tasks to process
     * @param stats the task statistics to update
     */
    private void processTasks(Page<Task> page, TaskStatistics stats) {
        page.stream().parallel().forEach(task -> {
            if (task.getDoneDate() == null || task.getCreationDate() == null) {
                return;
            }
    
            stats.incrementTotalDone();
            long elapsedTimeSeconds = task.getDoneDate().getEpochSecond() - task.getCreationDate().getEpochSecond();
            stats.addTotalDoneSeconds(elapsedTimeSeconds);
    
            switch (task.getPriority()) {
                case Priority.LOW:
                    stats.incrementLowDone();
                    stats.addLowDoneSeconds(elapsedTimeSeconds);
                    break;
                case Priority.MEDIUM:
                    stats.incrementMediumDone();
                    stats.addMediumDoneSeconds(elapsedTimeSeconds);
                    break;
                default:
                    stats.incrementHighDone();
                    stats.addHighDoneSeconds(elapsedTimeSeconds);
                    break;
            }
        });
    }

    /**
     * Calculates and sets the average times for tasks.
     *
     * @param stats the task statistics to update
     */
    private void calculateAndSetAverages(TaskStatistics stats) {
        if (stats.getTotalDone() > 0) {
            stats.setAverageDoneTime(formatAverageTime(stats.getTotalDoneSeconds() / stats.getTotalDone()));
        }
        if (stats.getTotalLowDone() > 0) {
            stats.setAverageLowDoneTime(formatAverageTime(stats.getLowDoneSeconds() / stats.getTotalLowDone()));
        }
        if (stats.getTotalMediumDone() > 0) {
            stats.setAverageMediumDoneTime(formatAverageTime(stats.getMediumDoneSeconds() / stats.getTotalMediumDone()));
        }
        if (stats.getTotalHighDone() > 0) {
            stats.setAverageHighDoneTime(formatAverageTime(stats.getHighDoneSeconds() / stats.getTotalHighDone()));
        }
    }

    /**
     * Parses the sort parameter into a Sort object.
     *
     * @param sortList the sort parameter
     * @return the Sort object
     */
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

    /**
     * Formats the average time in seconds into a human-readable format.
     *
     * @param averageTimeSeconds the average time in seconds
     * @return the formatted average time
     */
    private String formatAverageTime(Long averageTimeSeconds) {
        System.out.println("Time in seconds: " + averageTimeSeconds);
        long minutes = averageTimeSeconds / 60;
        long seconds = averageTimeSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
}
