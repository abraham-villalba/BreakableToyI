package com.todos.backend.backend_todos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.todos.backend.backend_todos.dto.NewTask;
import com.todos.backend.backend_todos.exceptions.TaskNotFoundException;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;
import com.todos.backend.backend_todos.repositories.TaskRepository;
import com.todos.backend.backend_todos.services.TaskService;

public class TaskServiceTest {

    @Mock
    private TaskRepository repository;


    @InjectMocks
    private TaskService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createValidNewTask_returnsNewTask() {
        // Arrange
        NewTask newTask = new NewTask();
        newTask.setText("Sample Task");
        newTask.setPriority(Priority.HIGH);

        Task savedTask = new Task();
        savedTask.setId(UUID.randomUUID());
        savedTask.setCreationDate(Instant.now());
        savedTask.setText(newTask.getText());
        savedTask.setPriority(newTask.getPriority());

        when(repository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = service.createTask(newTask);

        // Assert
        assertNotNull(result.getId(), "The id should not be null");
        assertEquals(newTask.getText(), result.getText(), "The text should match");
        assertEquals(newTask.getPriority(), result.getPriority(), "Priorities should match");
        assertNotNull(result.getCreationDate(),"Creation date is not set");
        verify(repository, times(1)).save(any(Task.class)); // Ensure repo is called once
    }

    @Test
    public void updateTask_WhenTaskExists_ShouldUpdateAndReturnTask() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        NewTask updatedTask = new NewTask();
        updatedTask.setText("Updated text");
        updatedTask.setPriority(Priority.MEDIUM);
        updatedTask.setDueDate(LocalDate.now());

        Task existingTask = new Task();
        existingTask.setId(existingId);
        existingTask.setText("Old text");
        existingTask.setPriority(Priority.LOW);
        existingTask.setCreationDate(LocalDate.now().minusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant());
        existingTask.setDueDate(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Task updatedEntity = new Task();
        updatedEntity.setId(existingId);
        updatedEntity.setText(updatedTask.getText());
        updatedEntity.setPriority(updatedTask.getPriority());
        updatedEntity.setDueDate(updatedTask.getDueDate().atStartOfDay(ZoneOffset.UTC).toInstant());

        // Mock repository behavior
        when(repository.findById(existingId)).thenReturn(Optional.of(existingTask));
        when(repository.save(existingTask)).thenReturn(updatedEntity);

        // Act
        Task result = service.updateTask(existingId, updatedTask);

        // Assert
        assertNotNull(result, "The updated Task should not be null");
        assertEquals(updatedTask.getText(), result.getText(), "The text should be updated");
        assertEquals(updatedTask.getPriority(), result.getPriority(), "The priority should be updated");
        assertEquals(updatedTask.getDueDate().atStartOfDay(ZoneOffset.UTC).toInstant(), result.getDueDate(), "The due date should be updated");

        // Verify interactions with the repository
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).save(existingTask);
    }

    @Test
    public void updateTask_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        NewTask updatedTask = new NewTask();
        updatedTask.setText("Updated text");
        updatedTask.setPriority(Priority.MEDIUM);
        updatedTask.setDueDate(LocalDate.now());

        // Mock repository behavior
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, 
            () -> service.updateTask(nonExistentId, updatedTask),
            "Expected a TaskNotFoundException to be thrown");

        assertEquals("To Do not found with id " + nonExistentId, exception.getMessage(), 
            "The exception message should match");

        // Verify findById was called but save was not
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

    @Test
    public void completeTask_WhenTaskExists_ShouldUpdateAndReturnTask() {
        // Arrange
        UUID existingId = UUID.randomUUID();

        Task existingTask = new Task();
        existingTask.setId(existingId);

        Task updatedEntity = new Task();
        updatedEntity.setId(existingId);
        updatedEntity.setDone(true);
        updatedEntity.setDoneDate(Instant.now());

        // Mock repository behavior
        when(repository.findById(existingId)).thenReturn(Optional.of(existingTask));
        when(repository.save(existingTask)).thenReturn(updatedEntity);

        // Act
        Task result = service.completeTask(existingId);

        // Assert
        assertNotNull(result, "The completed Task should not be null");
        assertEquals(updatedEntity.getDone(), result.getDone(), "Done should be set to true");
        assertNotNull(result.getDoneDate(), "Done date should be set");
        assertEquals(updatedEntity.getDoneDate(), result.getDoneDate(), "Date should match");
        

        // Verify interactions with the repository
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).save(existingTask);
    }

    @Test
    public void completeTask_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Mock repository behavior
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, 
            () -> service.completeTask(nonExistentId),
            "Expected a TaskNotFoundException to be thrown");

        assertEquals("To Do not found with id " + nonExistentId, exception.getMessage(), 
            "The exception message should match");

        // Verify findById was called but save was not
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

}
