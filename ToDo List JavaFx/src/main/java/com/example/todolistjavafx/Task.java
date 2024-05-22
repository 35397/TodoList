package com.example.todolistjavafx;

import java.time.LocalDate;
import java.time.LocalTime;

public class Task {
    private String description;
    private LocalDate deadline;
    private LocalTime time; // Добавляем поле времени
    private boolean isCompleted;

    public Task(String description, LocalDate deadline) {
        this.description = description;
        this.deadline = deadline;
        this.isCompleted = false;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    // Добавляем геттер и сеттер для времени
    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        String taskString = description + (deadline != null ? " (Due: " + deadline : "");
        if (time != null) {
            taskString += " " + time;
        }
        taskString += ")";
        return taskString;
    }
}
