package com.todos.backend.backend_todos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.repositories.ToDoRepository;

// Load the complete application or context
@SpringBootTest
@AutoConfigureMockMvc
public class ToDoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // TODO: Update with getById once it's created in the service layer
    @Autowired
    private ToDoRepository repository;

    @Test
    public void createWhenInvalidInput_thenReturnsBadRequestStatus() throws Exception {
        // Arrange
        NewToDo invalidToDo = new NewToDo();
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
        NewToDo validToDo = new NewToDo();
        validToDo.setText("Update API Documentation");
        validToDo.setPriority(Priority.MEDIUM);
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validToDo))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isOk()); // Compare it against the expected value
            // TODO: Add validation to ensure response contains information about the new ToDo created.
        ToDo createdToDo = repository.findAll().get(0); // This assumes that the db is empty when starting.
        assertEquals(validToDo.getText(), createdToDo.getText());
        assertEquals(validToDo.getPriority(), createdToDo.getPriority());

    }
}
