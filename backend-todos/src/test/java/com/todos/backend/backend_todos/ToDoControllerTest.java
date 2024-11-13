package com.todos.backend.backend_todos;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todos.backend.backend_todos.controllers.ToDoController;
import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.services.ToDoService;

@WebMvcTest(ToDoController.class)
public class ToDoControllerTest {

    // Simulates HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // Convert Object to JSON string
    @Autowired
    private ObjectMapper objectMapper;

    // Mocked service to avoid loading full context of the application
    @MockBean
    private ToDoService toDoService;

    @Test
    public void createWhenInvalidInput_thenReturnsBadRequestStatus() throws Exception {
        // Arrange
        NewToDo invalidToDo = new NewToDo();
        invalidToDo.setText(null);
        // Act & Assert
        // Simulate POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidToDo))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }

    @Test
    public void createWhenValidInput_thenReturnsOktStatus() throws Exception {
        // Arrange
        NewToDo validToDo = new NewToDo();
        validToDo.setText("Update API Documentation");
        validToDo.setPriority(Priority.MEDIUM);
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validToDo))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isOk()); // Compare it against the expected value
    }

    @Test
    public void createWhenNoInput_thenReturnsBadRequestStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}")) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }

    @Test
    public void createWhenDueDateInThePast_thenReturnsBadRequestStatus() throws Exception {
        // Act & Assert
        NewToDo invalidToDo = new NewToDo();
        invalidToDo.setText("Update API Documentation");
        invalidToDo.setPriority(Priority.MEDIUM);
        // Set to yesterday's date.
        invalidToDo.setDueDate(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 100));

        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidToDo))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }

    @Test
    public void createWhenInvalidPriority_thenReturnsBadRequestStatus() throws Exception {
        // Act & Assert
        NewToDo invalidToDo = new NewToDo();
        invalidToDo.setText("Update API Documentation");
        // Missing priority is not valid

        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidToDo))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }
    
}
