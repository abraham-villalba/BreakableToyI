package com.todos.backend.backend_todos.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.dto.ToDoStatistics;
import com.todos.backend.backend_todos.exceptions.ToDoNotFoundException;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.services.ToDoService;

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
public class ToDoController {

    @Autowired
    private ToDoService service;

    @PostMapping("/todos")
    public ToDo createToDo(@Valid @RequestBody NewToDo toDo) {
        return service.createToDo(toDo);
    }

    @PutMapping("/todos/{id}")
    public ToDo updateToDo(@PathVariable UUID id, @Valid @RequestBody NewToDo updatedToDo) throws ToDoNotFoundException {
        return service.updateToDo(id, updatedToDo);
    }

    @PutMapping("/todos/{id}/done")
    public ToDo completeToDo(@PathVariable UUID id) throws ToDoNotFoundException {
        return service.completeToDo(id);
    }

    @PutMapping("/todos/{id}/undone")
    public ToDo uncompleteToDo(@PathVariable UUID id) throws ToDoNotFoundException {
        return service.uncompleteToDo(id);
    }
    
    @GetMapping("/todos")
    public Page<ToDo> getAllToDosFilterAndSort(
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
    public void deleteToDo(@PathVariable UUID id) throws ToDoNotFoundException {
        service.deleteToDo(id);
    }

    @GetMapping("/todos/stats")
    public ToDoStatistics getStatistics() {
        return service.geToDoStatistics();
    }
    
}
