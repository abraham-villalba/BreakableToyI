package com.todos.backend.backend_todos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todos.backend.backend_todos.dto.NewToDo;
import com.todos.backend.backend_todos.models.Priority;
import com.todos.backend.backend_todos.models.ToDo;
import com.todos.backend.backend_todos.repositories.ToDoInMemoryRepository;
import com.todos.backend.backend_todos.repositories.ToDoRepository;
import com.todos.backend.backend_todos.services.ToDoService;

// Load the complete application or context
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = BackendTodosApplication.class)
public class ToDoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // @Autowired
    // private ToDoRepository repository;

    @Autowired
    private ToDoInMemoryRepository repository;

    @Autowired
    private ToDoService service;

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

        // Act
        ToDo newToDo = service.createToDo(validToDo);

        // Assert
        assertNotNull(newToDo, "New To Do should not be null");
        assertNotNull(newToDo.getId(), "The id should not be null");
        assertNotNull(newToDo.getCreationDate(), "Creation date should not be null");
        assertEquals(validToDo.getText(), newToDo.getText(), "Text should be equal");
        assertEquals(validToDo.getPriority(), newToDo.getPriority(), "Priority should be equal");
        System.out.println("Do i print something?");
        ToDo savedToDo = repository.findById(newToDo.getId()).orElse(null);
        assertNotNull(savedToDo, "ToDo wasn't saved");
        assertEquals(newToDo.getText(), savedToDo.getText());
        assertEquals(newToDo.getPriority(), savedToDo.getPriority());
        assertEquals(newToDo.getCreationDate(), savedToDo.getCreationDate());

    }

}
