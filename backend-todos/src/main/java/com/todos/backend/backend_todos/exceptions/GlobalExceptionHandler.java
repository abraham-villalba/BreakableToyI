package com.todos.backend.backend_todos.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    // NoResourceFoundException
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,Object> handleNoResourceFoundException(NoResourceFoundException e) {
        Map<String,Object> errors = new HashMap<>();
        errors.put("error", "URL Not Found");
        return errors;
    }

    // MethodArgumentTypeMismatchException
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        Map<String,Object> errors = new HashMap<>();
        errors.put("error", "The id provided is of incorrect type");
        return errors;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String,Object> handleGeneralException(Exception e) {
        Map<String,Object> errors = new HashMap<>();
        errors.put("error", "Internal Server Error: Something went wrong...");
        return errors;
    }
    
}
