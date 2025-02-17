package com.todos.backend.backend_todos.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@CrossOrigin(origins = "http://localhost:8080") 
public class TaskController {

    @Autowired
    private TaskService service;

    @PostMapping("/todos")
    public Task createToDo(@Valid @RequestBody NewTask task) {
        return service.createToDo(task);
    }

    @PutMapping("/todos/{id}")
    public Task updateToDo(@PathVariable UUID id, @Valid @RequestBody NewTask updatedToDo) throws TaskNotFoundException {
        return service.updateToDo(id, updatedToDo);
    }

    @PutMapping("/todos/{id}/done")
    public Task completeToDo(@PathVariable UUID id) throws TaskNotFoundException {
        return service.completeToDo(id);
    }

    @PutMapping("/todos/{id}/undone")
    public Task uncompleteToDo(@PathVariable UUID id) throws TaskNotFoundException {
        return service.uncompleteToDo(id);
    }
    
    @GetMapping("/todos")
    public Page<Task> getAllToDosFilterAndSort(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10", required = false) int size,
        @RequestParam(required = false) String text,
        @RequestParam(required = false) Priority priority,
        @RequestParam(required = false) Boolean done,
        @RequestParam(defaultValue = "", required = false) String sortBy
    ) {
        return service.getAllToDosFilterAndSort(page,size,done,text,priority,sortBy);
    }

    @DeleteMapping("/todos/{id}")
    public void deleteToDo(@PathVariable UUID id) throws TaskNotFoundException {
        service.deleteToDo(id);
    }

    @GetMapping("/todos/stats")
    public TaskStatistics getStatistics() {
        return service.geToDoStatistics();
    }
    
}
