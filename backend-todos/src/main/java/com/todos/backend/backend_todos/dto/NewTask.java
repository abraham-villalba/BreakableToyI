package com.todos.backend.backend_todos.dto;

import java.util.Date;

import com.todos.backend.backend_todos.models.Priority;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NewTask {
    @NotBlank
    @Size(min=3,max=120)
    @NotNull
    private String text;

    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @NotNull
    private Priority priority;

    public NewTask() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
