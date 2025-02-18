package com.todos.backend.backend_todos.dto;

public interface TaskStatisticsProjection {
    long getTotalDone();
    long getTotalDoneSeconds();
    long getTotalLowDoneSeconds();
    long getTotalLowDone();
    long getTotalMediumDoneSeconds();
    long getTotalMediumDone();
    long getTotalHighDoneSeconds();
    long getTotalHighDone();
}
