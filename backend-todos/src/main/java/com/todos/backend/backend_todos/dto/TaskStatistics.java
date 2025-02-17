package com.todos.backend.backend_todos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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

    public void incrementTotalDone() {
        this.totalDone++;
    }

    public void incrementHighDone() {
        this.totalHighDone++;
    }

    public void incrementMediumDone() {
        this.totalMediumDone++;
    }

    public void incrementLowDone() {
        this.totalLowDone++;
    }
}
