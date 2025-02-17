package com.todos.backend.backend_todos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todos.backend.backend_todos.dto.NewTask;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;
import com.todos.backend.backend_todos.repositories.TaskRepository;
import com.todos.backend.backend_todos.services.TaskService;

// Load the complete application or context
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = BackendTodosApplication.class)
public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository repository;


    @Autowired
    private TaskService service;

    @Test
    public void createWhenInvalidInput_thenReturnsBadRequestStatus() throws Exception {
        // Arrange
        NewTask invalidToDo = new NewTask();
        invalidToDo.setText("");
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidToDo)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createWhenValidInput_thenCreatesNewToDo() throws Exception {
        // Arrange
        NewTask validToDo = new NewTask();
        validToDo.setText("Update API Documentation");
        validToDo.setPriority(Priority.MEDIUM);

        // Act
        Task newTask = service.createToDo(validToDo);

        // Assert
        assertNotNull(newTask, "New To Do should not be null");
        assertNotNull(newTask.getId(), "The id should not be null");
        assertNotNull(newTask.getCreationDate(), "Creation date should not be null");
        assertEquals(validToDo.getText(), newTask.getText(), "Text should be equal");
        assertEquals(validToDo.getPriority(), newTask.getPriority(), "Priority should be equal");
        System.out.println("Do i print something?");
        Task savedToDo = repository.findById(newTask.getId()).orElse(null);
        assertNotNull(savedToDo, "Task wasn't saved");
        assertEquals(newTask.getText(), savedToDo.getText());
        assertEquals(newTask.getPriority(), savedToDo.getPriority());
        assertEquals(newTask.getCreationDate(), savedToDo.getCreationDate());

    }

}
