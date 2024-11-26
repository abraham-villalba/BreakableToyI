package com.todos.backend.backend_todos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.exceptions.ToDoNotFoundException;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.repositories.ToDoRepository;
import com.todos.backend.backend_todos.services.ToDoService;

public class ToDoServiceTest {

    @Mock
    private ToDoRepository repository;


    @InjectMocks
    private ToDoService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createValidNewToDo_returnsNewToDo() {
        // Arrange
        NewToDo newToDo = new NewToDo();
        newToDo.setText("Sample Task");
        newToDo.setPriority(Priority.HIGH);

        Date creationDate = new Date();

        ToDo savedToDo = new ToDo();
        savedToDo.setId(UUID.randomUUID());
        savedToDo.setCreationDate(creationDate);
        savedToDo.setText(newToDo.getText());
        savedToDo.setPriority(newToDo.getPriority());

        when(repository.save(any(ToDo.class))).thenReturn(savedToDo);

        // Act
        ToDo result = service.createToDo(newToDo);

        // Assert
        assertNotNull(result.getId(), "The id should not be null");
        assertEquals(newToDo.getText(), result.getText(), "The text should match");
        assertEquals(newToDo.getPriority(), result.getPriority(), "Priorities should match");
        assertNotNull(result.getCreationDate(),"Creation date is not set");
        verify(repository, times(1)).save(any(ToDo.class)); // Ensure repo is called once
    }

    @Test
    public void updateToDo_WhenToDoExists_ShouldUpdateAndReturnToDo() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        NewToDo updatedToDo = new NewToDo();
        updatedToDo.setText("Updated text");
        updatedToDo.setPriority(Priority.MEDIUM);
        updatedToDo.setDueDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        ToDo existingToDo = new ToDo();
        existingToDo.setId(existingId);
        existingToDo.setText("Old text");
        existingToDo.setPriority(Priority.LOW);
        existingToDo.setCreationDate(Date.from(LocalDate.now().minusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        existingToDo.setDueDate(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        ToDo updatedEntity = new ToDo();
        updatedEntity.setId(existingId);
        updatedEntity.setText(updatedToDo.getText());
        updatedEntity.setPriority(updatedToDo.getPriority());
        updatedEntity.setDueDate(updatedToDo.getDueDate());

        // Mock repository behavior
        when(repository.findById(existingId)).thenReturn(Optional.of(existingToDo));
        when(repository.save(existingToDo)).thenReturn(updatedEntity);

        // Act
        ToDo result = service.updateToDo(existingId, updatedToDo);

        // Assert
        assertNotNull(result, "The updated ToDo should not be null");
        assertEquals(updatedToDo.getText(), result.getText(), "The text should be updated");
        assertEquals(updatedToDo.getPriority(), result.getPriority(), "The priority should be updated");
        assertEquals(updatedToDo.getDueDate(), result.getDueDate(), "The due date should be updated");

        // Verify interactions with the repository
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).save(existingToDo);
    }

    @Test
    public void updateToDo_WhenToDoDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        NewToDo updatedToDo = new NewToDo();
        updatedToDo.setText("Updated text");
        updatedToDo.setPriority(Priority.MEDIUM);
        updatedToDo.setDueDate(new Date());

        // Mock repository behavior
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ToDoNotFoundException exception = assertThrows(ToDoNotFoundException.class, 
            () -> service.updateToDo(nonExistentId, updatedToDo),
            "Expected a ToDoNotFoundException to be thrown");

        assertEquals("To Do not found with id " + nonExistentId, exception.getMessage(), 
            "The exception message should match");

        // Verify findById was called but save was not
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

    @Test
    public void completeToDo_WhenToDoExists_ShouldUpdateAndReturnToDo() {
        // Arrange
        UUID existingId = UUID.randomUUID();

        ToDo existingToDo = new ToDo();
        existingToDo.setId(existingId);

        ToDo updatedEntity = new ToDo();
        updatedEntity.setId(existingId);
        updatedEntity.setDone(true);
        updatedEntity.setDoneDate(new Date());

        // Mock repository behavior
        when(repository.findById(existingId)).thenReturn(Optional.of(existingToDo));
        when(repository.save(existingToDo)).thenReturn(updatedEntity);

        // Act
        ToDo result = service.completeToDo(existingId);

        // Assert
        assertNotNull(result, "The completed ToDo should not be null");
        assertEquals(updatedEntity.getDone(), result.getDone(), "Done should be set to true");
        assertNotNull(result.getDoneDate(), "Done date should be set");
        assertEquals(updatedEntity.getDoneDate(), result.getDoneDate(), "Date should match");
        

        // Verify interactions with the repository
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).save(existingToDo);
    }

    @Test
    public void completeToDo_WhenToDoDoesNotExist_ShouldThrowException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Mock repository behavior
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ToDoNotFoundException exception = assertThrows(ToDoNotFoundException.class, 
            () -> service.completeToDo(nonExistentId),
            "Expected a ToDoNotFoundException to be thrown");

        assertEquals("To Do not found with id " + nonExistentId, exception.getMessage(), 
            "The exception message should match");

        // Verify findById was called but save was not
        verify(repository, times(1)).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

}
