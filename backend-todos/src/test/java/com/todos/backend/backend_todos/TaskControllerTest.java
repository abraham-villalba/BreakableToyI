package com.todos.backend.backend_todos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todos.backend.backend_todos.controllers.TaskController;
import com.todos.backend.backend_todos.dto.NewTask;
import com.todos.backend.backend_todos.dto.TaskStatistics;
import com.todos.backend.backend_todos.exceptions.TaskNotFoundException;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.Task;
import com.todos.backend.backend_todos.services.TaskService;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    // Simulates HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // Convert Object to JSON string
    @Autowired
    private ObjectMapper objectMapper;

    // Mocked service to avoid loading full context of the application
    @MockBean
    private TaskService taskService;

    @Test
    public void createWhenInvalidInput_thenReturnsBadRequestStatus() throws Exception {
        // Arrange
        NewTask invalidTask = new NewTask();
        invalidTask.setText(null);
        // Act & Assert
        // Simulate POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidTask))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }

    @Test
    public void createWhenValidInput_thenReturnsOktStatus() throws Exception {
        // Arrange
        NewTask validTask = new NewTask();
        validTask.setText("Update API Documentation");
        validTask.setPriority(Priority.MEDIUM);
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validTask))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isCreated()); // Compare it against the expected value
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
    public void createWhenInvalidPriority_thenReturnsBadRequestStatus() throws Exception {
        // Arrange
        NewTask invalidTask = new NewTask();
        invalidTask.setText("Update API Documentation");
        // Missing priority is not valid

        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidTask))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }

    @Test
    public void updateWhenValidInput_thenReturnsOKStatus() throws Exception {
        // Arrange
        UUID existingId = UUID.randomUUID();
        NewTask updatedTask = new NewTask();
        updatedTask.setText("Updating the text field!");
        updatedTask.setPriority(Priority.LOW);
        
        // Mock Service Layer response
        Task updatedTaskResponse = new Task();
        updatedTaskResponse.setId(existingId);
        updatedTaskResponse.setText(updatedTask.getText());
        updatedTaskResponse.setPriority(updatedTask.getPriority());
        when(taskService.updateTask(eq(existingId), any(NewTask.class))).thenReturn(updatedTaskResponse);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + existingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedTask)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text").value(updatedTaskResponse.getText()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.priority").value(updatedTaskResponse.getPriority().toString()));
    }
    
    @Test
    public void updateWhenInvalidInput_thenReturnsBadRequest() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        NewTask invalidUpdatedTask = new NewTask();
        invalidUpdatedTask.setText(null);
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUpdatedTask))) // Costruct the body of the request
            .andExpect(MockMvcResultMatchers.status().isBadRequest()); // Compare it against the expected value
    }

    @Test
    public void updateWhenTaskDoesNotExist_thenReturnsNotFoundStatus() throws Exception {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        NewTask updatedTask = new NewTask();
        updatedTask.setText("Updating the text field!");
        updatedTask.setPriority(Priority.LOW);
        
        // Mock Service Layer response
        when(taskService.updateTask(eq(nonExistingId), any(NewTask.class))).thenThrow(new TaskNotFoundException("To Do not found with id " + nonExistingId));

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + nonExistingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedTask)))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("To Do not found with id " + nonExistingId));
    }

    @Test
    public void markAsDoneTaskExists_thenReturnsOkStatus() throws Exception {
        // Arrange
        UUID existingId = UUID.randomUUID();
        
        // Mock Service Layer response
        Task updatedTaskResponse = new Task();
        updatedTaskResponse.setId(existingId);
        updatedTaskResponse.setDone(true);
        Date doneDate = new Date();
        String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(doneDate);
        updatedTaskResponse.setDoneDate(doneDate);
        when(taskService.completeTask(eq(existingId))).thenReturn(updatedTaskResponse);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + existingId + "/done"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.done").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.doneDate").value(strDate));
    }

    @Test
    public void markAsDoneTaskDoesNotExist_thenReturnsNotFoundStatus() throws Exception {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        
        // Mock Service Layer response
        when(taskService.completeTask(eq(nonExistingId))).thenThrow(new TaskNotFoundException("To Do not found with id " + nonExistingId));

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + nonExistingId + "/done"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("To Do not found with id " + nonExistingId));
    }

    // LLM provided tests
    @Test
    public void uncompleteTaskExists_thenReturnsOkStatus() throws Exception {
        // Arrange
        UUID existingId = UUID.randomUUID();
        
        // Mock Service Layer response
        Task updatedTaskResponse = new Task();
        updatedTaskResponse.setId(existingId);
        updatedTaskResponse.setDone(false);
        when(taskService.uncompleteTask(eq(existingId))).thenReturn(updatedTaskResponse);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + existingId + "/undone"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.done").value(false));
    }

    @Test
    public void uncompleteTaskDoesNotExist_thenReturnsNotFoundStatus() throws Exception {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        
        // Mock Service Layer response
        when(taskService.uncompleteTask(eq(nonExistingId))).thenThrow(new TaskNotFoundException("Task not found with id " + nonExistingId));

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/todos/" + nonExistingId + "/undone"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Task not found with id " + nonExistingId));
    }

    @Test
    public void getAllTasksFilterAndSort_thenReturnsOkStatus() throws Exception {
        // Arrange
        Page<Task> tasks = Page.empty();
        when(taskService.getAllTasksFilterAndSort(anyInt(), anyInt(), any(), any(), any(), any())).thenReturn(tasks);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/todos"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }

    @Test
    public void deleteTaskExists_thenReturnsNoContentStatus() throws Exception {
        // Arrange
        UUID existingId = UUID.randomUUID();
        
        // Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/todos/" + existingId))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteTaskDoesNotExist_thenReturnsNotFoundStatus() throws Exception {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        
        // Mock Service Layer response
        doThrow(new TaskNotFoundException("To Do not found with id " + nonExistingId)).when(taskService).deleteTask(eq(nonExistingId));

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/todos/" + nonExistingId))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("To Do not found with id " + nonExistingId));
    }

    @Test
    public void getStatistics_thenReturnsOkStatus() throws Exception {
        // Arrange
        TaskStatistics stats = new TaskStatistics();
        when(taskService.geTaskStatistics()).thenReturn(stats);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/todos/stats"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalDone").value(stats.getTotalDone()));
    }
    
}
