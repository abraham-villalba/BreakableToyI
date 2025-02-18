package com.todos.backend.backend_todos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

        assertEquals("Task not found with id " + nonExistentId, exception.getMessage(), 
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

        assertEquals("Task not found with id " + nonExistentId, exception.getMessage(), 
            "The exception message should match");

        // Verify findById was called but save was not
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

    @Test
    public void deleteTask_WhenTaskExists_ShouldDeleteTask() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        Task existingTask = new Task();
        existingTask.setId(existingId);

        // Mock repository behavior
        when(repository.findById(existingId)).thenReturn(Optional.of(existingTask));

        // Act
        service.deleteTask(existingId);

        // Assert
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).delete(existingTask);
    }

    @Test
    public void deleteTask_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Mock repository behavior
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, 
            () -> service.deleteTask(nonExistentId),
            "Expected a TaskNotFoundException to be thrown");

        assertEquals("Task not found with id " + nonExistentId, exception.getMessage(), 
            "The exception message should match");

        // Verify findById was called but delete was not
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).delete(any());
    }

    @Test
    public void getAllTasksFilterAndSort_ShouldReturnFilteredAndSortedTasks() {
        // Arrange
        int page = 0;
        int size = 10;
        Boolean doneFilter = true;
        String textFilter = "Sample";
        Priority priorityFilter = Priority.HIGH;
        String sortList = "priority:asc";

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("priority")));
        Page<Task> taskPage = Page.empty(pageable);

        // Mock repository behavior
        when(repository.findByDoneTextAndPriority(doneFilter, textFilter, priorityFilter, pageable)).thenReturn(taskPage);

        // Act
        Page<Task> result = service.getAllTasksFilterAndSort(page, size, doneFilter, textFilter, priorityFilter, sortList);

        // Assert
        assertNotNull(result, "The result should not be null");
        verify(repository, times(1)).findByDoneTextAndPriority(doneFilter, textFilter, priorityFilter, pageable);
    }

    @Test
    public void getAllTasksFilterAndSort_WithInvalidSortField_ShouldThrowException() {
        // Arrange
        int page = 0;
        int size = 10;
        Boolean doneFilter = true;
        String textFilter = "Sample";
        Priority priorityFilter = Priority.HIGH;
        String sortList = "invalidField:asc";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> service.getAllTasksFilterAndSort(page, size, doneFilter, textFilter, priorityFilter, sortList),
            "Expected an IllegalArgumentException to be thrown");

        assertEquals("Invalid sort field: invalidField", exception.getMessage(), 
            "The exception message should match");
    }

    @Test
    public void uncompleteTask_WhenTaskExists_ShouldUpdateAndReturnTask() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        Task existingTask = new Task();
        existingTask.setId(existingId);
        existingTask.setDone(true);
        existingTask.setDoneDate(Instant.now());

        Task updatedEntity = new Task();
        updatedEntity.setId(existingId);
        updatedEntity.setDone(false);
        updatedEntity.setDoneDate(null);

        // Mock repository behavior
        when(repository.findById(existingId)).thenReturn(Optional.of(existingTask));
        when(repository.save(existingTask)).thenReturn(updatedEntity);

        // Act
        Task result = service.uncompleteTask(existingId);

        // Assert
        assertNotNull(result, "The uncompleted Task should not be null");
        assertEquals(updatedEntity.getDone(), result.getDone(), "Done should be set to false");
        assertNull(result.getDoneDate(), "Done date should be null");

        // Verify interactions with the repository
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).save(existingTask);
    }

    @Test
    public void uncompleteTask_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Mock repository behavior
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, 
            () -> service.uncompleteTask(nonExistentId),
            "Expected a TaskNotFoundException to be thrown");

        assertEquals("Task not found with id " + nonExistentId, exception.getMessage(), 
            "The exception message should match");

        // Verify findById was called but save was not
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

}
