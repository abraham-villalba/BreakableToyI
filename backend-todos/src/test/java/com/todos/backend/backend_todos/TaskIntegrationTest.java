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
        NewTask invalidTask = new NewTask();
        invalidTask.setText("");
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidTask)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createWhenValidInput_thenCreatesNewTask() throws Exception {
        // Arrange
        NewTask validTask = new NewTask();
        validTask.setText("Update API Documentation");
        validTask.setPriority(Priority.MEDIUM);

        // Act
        Task newTask = service.createTask(validTask);

        // Assert
        assertNotNull(newTask, "New To Do should not be null");
        assertNotNull(newTask.getId(), "The id should not be null");
        assertNotNull(newTask.getCreationDate(), "Creation date should not be null");
        assertEquals(validTask.getText(), newTask.getText(), "Text should be equal");
        assertEquals(validTask.getPriority(), newTask.getPriority(), "Priority should be equal");
        Task savedTask = repository.findById(newTask.getId()).orElse(null);
        assertNotNull(savedTask, "Task wasn't saved");
        assertEquals(newTask.getText(), savedTask.getText());
        assertEquals(newTask.getPriority(), savedTask.getPriority());
        assertEquals(newTask.getCreationDate(), savedTask.getCreationDate());

    }

}
