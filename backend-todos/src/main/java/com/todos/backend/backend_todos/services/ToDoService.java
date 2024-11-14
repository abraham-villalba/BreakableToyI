package com.todos.backend.backend_todos.services;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import com.todos.backend.backend_todos.models.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.exceptions.ToDoNotFoundException;
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

    public Page<ToDo> getAllToDosFilterByDoneTextAndPriority(
        Integer page,
        Integer size,
        Boolean doneFilter, 
        String textFilter, 
        Priority priorityFilter,
        String sortList 
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByDoneTextAndPriority(doneFilter, textFilter, priorityFilter, pageable);
    }
    
}
