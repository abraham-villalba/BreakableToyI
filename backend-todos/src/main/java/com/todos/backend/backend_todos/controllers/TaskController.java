package com.todos.backend.backend_todos.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.todos.backend.backend_todos.dto.NewTask;
import com.todos.backend.backend_todos.dto.TaskStatistics;
import com.todos.backend.backend_todos.exceptions.TaskNotFoundException;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;
import com.todos.backend.backend_todos.services.TaskService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling task-related operations.
 */
@RestController
public class TaskController {

    private final TaskService service;

    /**
     * Constructs a new TaskController with the given TaskService.
     *
     * @param service the task service to use
     */
    public TaskController(TaskService service) {
        this.service = service;
    }

    /**
     * Creates a new task.
     *
     * @param task the task to create
     * @return the created task
     */
    @PostMapping("/todos")
    public ResponseEntity<Task> createTask(@Valid @RequestBody NewTask task) {
        Task createdTask = service.createTask(task);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Updates an existing task.
     *
     * @param id the ID of the task to update
     * @param updatedTask the updated task details
     * @return the updated task
     * @throws TaskNotFoundException if the task with the specified ID is not found
     */
    @PutMapping("/todos/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable UUID id, @Valid @RequestBody NewTask updatedTask) throws TaskNotFoundException {
        Task updated = service.updateTask(id, updatedTask);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /**
     * Marks a task as done.
     *
     * @param id the ID of the task to mark as done
     * @return the task that was marked as done
     * @throws TaskNotFoundException if the task with the specified ID is not found
     */
    @PutMapping("/todos/{id}/done")
    public ResponseEntity<Task> completeTask(@PathVariable UUID id) throws TaskNotFoundException {
        Task completedTask = service.completeTask(id);
        return new ResponseEntity<>(completedTask, HttpStatus.OK);
    }

    /**
     * Marks a task as not done.
     *
     * @param id the ID of the task to mark as not done
     * @return the task that was marked as not done
     * @throws TaskNotFoundException if the task with the specified ID is not found
     */
    @PutMapping("/todos/{id}/undone")
    public ResponseEntity<Task> uncompleteTask(@PathVariable UUID id) throws TaskNotFoundException {
        Task uncompletedTask = service.uncompleteTask(id);
        return new ResponseEntity<>(uncompletedTask, HttpStatus.OK);
    }
    
    /**
     * Retrieves all tasks, optionally filtered and sorted.
     *
     * @param page the page number to retrieve
     * @param size the number of tasks per page
     * @param text the text to filter by
     * @param priority the priority to filter by
     * @param done the done status to filter by
     * @param sortBy the field to sort by
     * @return a page of tasks
     */
    @GetMapping("/todos")
    public ResponseEntity<Page<Task>> getAllTasksFilterAndSort(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10", required = false) int size,
        @RequestParam(required = false) String text,
        @RequestParam(required = false) Priority priority,
        @RequestParam(required = false) Boolean done,
        @RequestParam(defaultValue = "", required = false) String sortBy
    ) {
        Page<Task> tasks = service.getAllTasksFilterAndSort(page,size,done,text,priority,sortBy);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * Deletes a task.
     *
     * @param id the ID of the task to delete
     * @throws TaskNotFoundException if the task with the specified ID is not found
     */
    @DeleteMapping("/todos/{id}")
    public void deleteTask(@PathVariable UUID id) throws TaskNotFoundException {
        service.deleteTask(id);
    }

    /**
     * Retrieves task statistics.
     *
     * @return the task statistics
     */
    @GetMapping("/todos/stats")
    public ResponseEntity<TaskStatistics> getStatistics() {
        TaskStatistics stats = service.geTaskStatistics();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
    
}
