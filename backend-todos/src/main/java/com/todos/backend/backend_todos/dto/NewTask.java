package com.todos.backend.backend_todos.dto;

import java.util.Date;

import com.todos.backend.backend_todos.models.Priority;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewTask {
    @NotBlank
    @Size(min = 3, max = 120)
    @NotNull
    private String text;

    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @NotNull
    private Priority priority;
}
