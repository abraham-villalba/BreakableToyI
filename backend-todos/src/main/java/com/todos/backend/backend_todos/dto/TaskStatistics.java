package com.todos.backend.backend_todos.dto;

public class TaskStatistics {
    private Integer totalDone;
    private Integer totalLowDone;
    private Integer totalMediumDone;
    private Integer totalHighDone;
    private String averageDoneTime;
    private String averageLowDoneTime;
    private String averageMediumDoneTime;
    private String averageHighDoneTime;

    public TaskStatistics() {
        this.averageDoneTime = "";
        this.averageLowDoneTime = "";
        this.averageMediumDoneTime = "";
        this.averageHighDoneTime = "";
        this.totalDone = 0;
        this.totalHighDone = 0;
        this.totalMediumDone = 0;
        this.totalLowDone = 0;
    }

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

    public Integer getTotalDone() {
        return totalDone;
    }
    public void setTotalDone(Integer totalDone) {
        this.totalDone = totalDone;
    }
    public Integer getTotalLowDone() {
        return totalLowDone;
    }
    public void setTotalLowDone(Integer totalLowDone) {
        this.totalLowDone = totalLowDone;
    }
    public Integer getTotalMediumDone() {
        return totalMediumDone;
    }
    public void setTotalMediumDone(Integer totalMediumDone) {
        this.totalMediumDone = totalMediumDone;
    }
    public Integer getTotalHighDone() {
        return totalHighDone;
    }
    public void setTotalHighDone(Integer totalHighDone) {
        this.totalHighDone = totalHighDone;
    }
    public String getAverageDoneTime() {
        return averageDoneTime;
    }
    public void setAverageDoneTime(String averageDoneTime) {
        this.averageDoneTime = averageDoneTime;
    }
    public String getAverageLowDoneTime() {
        return averageLowDoneTime;
    }
    public void setAverageLowDoneTime(String averageLowDoneTime) {
        this.averageLowDoneTime = averageLowDoneTime;
    }
    public String getAverageMediumDoneTime() {
        return averageMediumDoneTime;
    }
    public void setAverageMediumDoneTime(String averageMediumDoneTime) {
        this.averageMediumDoneTime = averageMediumDoneTime;
    }
    public String getAverageHighDoneTime() {
        return averageHighDoneTime;
    }
    public void setAverageHighDoneTime(String averageHighDoneTime) {
        this.averageHighDoneTime = averageHighDoneTime;
    }

    
}
