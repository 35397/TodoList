package com.example.todolistjavafx;

import javafx.scene.control.ListCell;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TaskListCell extends ListCell<Task> {

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            setText(null);
            setStyle("");
        } else {
            setText(task.toString());
            updateCellStyle(task);
        }
    }

    private void updateCellStyle(Task task) {
        LocalDate deadline = task.getDeadline();
        if (deadline != null) {
            long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
            if (daysUntilDeadline <= 0) {
                setStyle("-fx-background-color: lightcoral;");
            } else if (daysUntilDeadline <= 3) {
                setStyle("-fx-background-color: yellow;");
            } else {
                setStyle("");
            }
        } else {
            setStyle("");
        }
        if (task.isCompleted()) {
            setStyle("-fx-background-color: lightgreen;");
        }
    }
}
