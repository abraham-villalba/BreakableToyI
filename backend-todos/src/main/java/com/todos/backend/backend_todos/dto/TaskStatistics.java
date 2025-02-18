package com.todos.backend.backend_todos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Represents the statistics of tasks.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatistics {
    @Builder.Default
    private Integer totalDone = 0;
    
    @Builder.Default
    private Integer totalLowDone = 0;
    
    @Builder.Default
    private Integer totalMediumDone = 0;
    
    @Builder.Default
    private Integer totalHighDone = 0;
    
    @Builder.Default
    private String averageDoneTime = "";
    
    @Builder.Default
    private String averageLowDoneTime = "";
    
    @Builder.Default
    private String averageMediumDoneTime = "";
    
    @Builder.Default
    private String averageHighDoneTime = "";

    @JsonIgnore
    @Builder.Default
    private Long totalDoneSeconds = 0L;
    
    @JsonIgnore
    @Builder.Default
    private Long lowDoneSeconds = 0L;
    
    @JsonIgnore
    @Builder.Default
    private Long mediumDoneSeconds = 0L;
    
    @JsonIgnore
    @Builder.Default
    private Long highDoneSeconds = 0L;

    /**
     * Increments the total number of tasks done.
     */
    public void incrementTotalDone() {
        this.totalDone++;
    }

    /**
     * Increments the total number of high priority tasks done.
     */
    public void incrementHighDone() {
        this.totalHighDone++;
    }

    /**
     * Increments the total number of medium priority tasks done.
     */
    public void incrementMediumDone() {
        this.totalMediumDone++;
    }

    /**
     * Increments the total number of low priority tasks done.
     */
    public void incrementLowDone() {
        this.totalLowDone++;
    }

    /**
     * Adds the total number of seconds spent on a task.
     */
    public void addTotalDoneSeconds(Long seconds) {
        this.totalDoneSeconds += seconds;
    }

    /**
     * Adds the total number of seconds spent on a low priority task.
     */
    public void addLowDoneSeconds(Long seconds) {
        this.lowDoneSeconds += seconds;
    }

    /**
     * Adds the total number of seconds spent on a medium priority task.
     */
    public void addMediumDoneSeconds(Long seconds) {
        this.mediumDoneSeconds += seconds;
    }

    /**
     * Adds the total number of seconds spent on a high priority task.
     */
    public void addHighDoneSeconds(Long seconds) {
        this.highDoneSeconds += seconds;
    }
}
