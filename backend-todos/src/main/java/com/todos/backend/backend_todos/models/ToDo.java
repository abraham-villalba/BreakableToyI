package com.todos.backend.backend_todos.models;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


@Entity
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Temporal(TemporalType.DATE)
    private Date creationDate;
    
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    
    @Temporal(TemporalType.DATE)
    private Date doneDate;
    
    private String text;
    private Boolean done;
    private Priority priority;
    
    // Constructor
    public ToDo() {
    }
    
    // Setters and Getters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public Date getDueDate() {
        return dueDate;
    }
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    public Date getDoneDate() {
        return doneDate;
    }
    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Boolean getDone() {
        return done;
    }
    public void setDone(Boolean done) {
        this.done = done;
    }
    public Priority getPriority() {
        return priority;
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

}
