package com.todos.backend.backend_todos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
import com.todos.backend.backend_todos.exceptions.ToDoNotFoundException;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.ToDo;
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
        // Arrange
        NewToDo invalidToDo = new NewToDo();
        invalidToDo.setText("Update API Documentation");
        // Missing priority is not valid

        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidToDo))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }

    @Test
    public void updateWhenValidInput_thenReturnsOKStatus() throws Exception {
        // Arrange
        UUID existingId = UUID.randomUUID();
        NewToDo updatedToDo = new NewToDo();
        updatedToDo.setText("Updating the text field!");
        updatedToDo.setPriority(Priority.LOW);
        
        // Mock Service Layer response
        ToDo updatedToDoResponse = new ToDo();
        updatedToDoResponse.setId(existingId);
        updatedToDoResponse.setText(updatedToDo.getText());
        updatedToDoResponse.setPriority(updatedToDo.getPriority());
        when(toDoService.updateToDo(eq(existingId), any(NewToDo.class))).thenReturn(updatedToDoResponse);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedToDo)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text").value(updatedToDoResponse.getText()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.priority").value(updatedToDoResponse.getPriority().toString()));
    }
    
    @Test
    public void updateWhenInvalidInput_thenReturnsBadRequest() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        NewToDo invalidUpdatedToDo = new NewToDo();
        invalidUpdatedToDo.setText(null);
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUpdatedToDo))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }

    @Test
    public void updateWhenToDoDoesNotExist_thenReturnsNotFoundStatus() throws Exception {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        NewToDo updatedToDo = new NewToDo();
        updatedToDo.setText("Updating the text field!");
        updatedToDo.setPriority(Priority.LOW);
        
        // Mock Service Layer response
        when(toDoService.updateToDo(eq(nonExistingId), any(NewToDo.class))).thenThrow(new ToDoNotFoundException("To Do not found with id " + nonExistingId));

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + nonExistingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedToDo)))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("To Do not found with id " + nonExistingId));
    }

    @Test
    public void markAsDoneToDoExists_thenReturnsOkStatus() throws Exception {
        // Arrange
        UUID existingId = UUID.randomUUID();
        
        // Mock Service Layer response
        ToDo updatedToDoResponse = new ToDo();
        updatedToDoResponse.setId(existingId);
        updatedToDoResponse.setDone(true);
        Date doneDate = new Date();
        String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(doneDate);
        updatedToDoResponse.setDoneDate(doneDate);
        when(toDoService.completeToDo(eq(existingId))).thenReturn(updatedToDoResponse);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + existingId + "/done"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.done").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.doneDate").value(strDate));
    }

    @Test
    public void markAsDoneToDoDoesNotExist_thenReturnsNotFoundStatus() throws Exception {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        
        // Mock Service Layer response
        when(toDoService.completeToDo(eq(nonExistingId))).thenThrow(new ToDoNotFoundException("To Do not found with id " + nonExistingId));

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + nonExistingId + "/done"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("To Do not found with id " + nonExistingId));
    }

    
}
