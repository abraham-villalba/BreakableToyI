package com.todos.backend.backend_todos.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.repositories.ToDoRepository;

@Service
public class ToDoService {
    @Autowired
    private ToDoRepository repository;

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
    
}
