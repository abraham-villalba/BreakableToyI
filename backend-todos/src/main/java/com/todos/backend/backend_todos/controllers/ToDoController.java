package com.todos.backend.backend_todos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.services.ToDoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class ToDoController {

    @Autowired
    private ToDoService service;

    @PostMapping("/todos")
    public ToDo postMethodName(@RequestBody ToDo toDo) {
        return service.saveToDo(toDo);
    }
    
}
