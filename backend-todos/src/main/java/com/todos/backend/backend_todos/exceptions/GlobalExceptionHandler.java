package com.todos.backend.backend_todos.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> handleInvalidArgument(MethodArgumentNotValidException e) {
        Map<String,Object> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return errors;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> handleInvalidPriority(HttpMessageNotReadableException e) {
        Map<String,Object> errors = new HashMap<>();
        errors.put("priority", e.getMessage());
        // e.getBindingResult().getFieldErrors().forEach(error -> {
        //     errors.put(error.getField(), error.getDefaultMessage());
        // });
        return errors;
    }

    @ExceptionHandler(ToDoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,Object> handleToDoNotFound(ToDoNotFoundException e) {
        Map<String,Object> errors = new HashMap<>();
        errors.put("error", e.getMessage());
        return errors;
    }
}
