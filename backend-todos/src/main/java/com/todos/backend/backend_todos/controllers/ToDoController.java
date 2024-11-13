package com.todos.backend.backend_todos.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.exceptions.ToDoNotFoundException;
import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.services.ToDoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class ToDoController {

    @Autowired
    private ToDoService service;

    @PostMapping("/todos")
    public ToDo createToDo(@Valid @RequestBody NewToDo toDo) {
        return service.createToDo(toDo);
    }

    // TODO: Add validation for uuid url
    @PutMapping("/todos/{id}")
    public ToDo updateToDo(@PathVariable UUID id, @Valid @RequestBody NewToDo updatedToDo) throws ToDoNotFoundException {
        //TODO: process PUT request
        return service.updateToDo(id, updatedToDo);
    }

    @PutMapping("/todos/{id}/done")
    public ToDo completeToDo(@PathVariable UUID id) throws ToDoNotFoundException {
        //TODO: process PUT request
        return service.completeToDo(id);
    }

    @PutMapping("/todos/{id}/undone")
    public ToDo uncompleteToDo(@PathVariable UUID id) throws ToDoNotFoundException {
        //TODO: process PUT request
        return service.uncompleteToDo(id);
    }
    
}
