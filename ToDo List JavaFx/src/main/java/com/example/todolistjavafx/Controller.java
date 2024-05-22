package com.example.todolistjavafx;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Arrays;

public class Controller implements Initializable {

    @FXML private ListView<Task> taskList;
    @FXML private TextField newTask;
    @FXML private DatePicker deadlinePicker;
    private String filePath = "data.txt";
    private File data = new File(filePath);

    public void addNewTask(ActionEvent e) {
        String text = newTask.getText();
        LocalDate deadline = deadlinePicker.getValue();
        if (!text.equals("")) {
            taskList.getItems().add(new Task(text, deadline));
            newTask.setText("");
            deadlinePicker.setValue(null);
        } else {
            System.out.println("No input..");
        }
    }

    public void deleteTask(ActionEvent e) {
        Task selected = taskList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            taskList.getItems().remove(selected);
        } else {
            System.out.println("No task selected..");
        }
    }

    public void markTaskCompleted(ActionEvent e) {
        Task selected = taskList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setCompleted(true);
            taskList.refresh();
        } else {
            System.out.println("No task selected..");
        }
    }

    public void exitProgram(ActionEvent e) {
        System.out.println("Exiting program..");
        ObservableList<Task> currentTaskList = taskList.getItems();

        try (FileWriter writer = new FileWriter(filePath)) {
            for (Task task : currentTaskList) {
                writer.write(task.getDescription() + ";" + task.getDeadline() + ";" + task.getTime() + ";" + task.isCompleted() + "\n");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Task> call(ListView<Task> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Task task, boolean empty) {
                        super.updateItem(task, empty);
                        if (empty || task == null) {
                            setText(null);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(task.getDescription());
                            LocalDate deadline = task.getDeadline();
                            if (deadline != null) {
                                stringBuilder.append(" - ").append(deadline);
                                LocalTime time = task.getTime();
                                if (time != null) {
                                    stringBuilder.append(" ").append(time);
                                }
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
                            setText(stringBuilder.toString());
                        }
                    }
                };
            }
        });

        try (Scanner myReader = new Scanner(data)) {
            while (myReader.hasNextLine()) {
                String[] taskData = myReader.nextLine().split(";");
                if (taskData.length >= 4) {
                    String description = taskData[0];
                    LocalDate deadline = taskData[1].equals("null") ? null : LocalDate.parse(taskData[1]);
                    LocalTime time = taskData[2].equals("null") ? null : LocalTime.parse(taskData[2]);
                    boolean isCompleted = Boolean.parseBoolean(taskData[3]);
                    Task task = new Task(description, deadline);
                    task.setTime(time);
                    task.setCompleted(isCompleted);
                    taskList.getItems().add(task);
                } else {
                    // Handle invalid data or log an error
                    System.err.println("Invalid task data: " + Arrays.toString(taskData));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
