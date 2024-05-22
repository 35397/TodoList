package com.example.todolistjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    @FXML private ListView<Task> taskList;
    @FXML private TextField newTask;
    @FXML private DatePicker deadlinePicker;
    @FXML private ChoiceBox<String> sortChoiceBox;
    private String filePath = "data.txt";
    private File data = new File(filePath);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskList.setCellFactory(param -> new TaskListCell());

        sortChoiceBox.getItems().addAll("Sort by Date", "Sort by Task Name");
        sortChoiceBox.setValue("Sort by Date"); // default selection

        // Add listener to sortChoiceBox to sort tasks when the selected option changes
        sortChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> sortTasks());

        loadDataFromFile();
    }

    private void loadDataFromFile() {
        try (Scanner myReader = new Scanner(data)) {
            while (myReader.hasNextLine()) {
                String[] taskData = myReader.nextLine().split(";");
                if (taskData.length >= 4) {
                    String description = taskData[0];
                    LocalDate deadline = null;
                    LocalTime time = null;
                    boolean isCompleted = Boolean.parseBoolean(taskData[3]);

                    try {
                        if (!"null".equals(taskData[1])) {
                            deadline = LocalDate.parse(taskData[1]);
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format: " + taskData[1]);
                    }

                    try {
                        if (!"null".equals(taskData[2])) {
                            time = LocalTime.parse(taskData[2]);
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid time format: " + taskData[2]);
                    }

                    Task task = new Task(description, deadline);
                    task.setTime(time);
                    task.setCompleted(isCompleted);
                    taskList.getItems().add(task);
                } else {
                    System.err.println("Invalid task data: " + String.join(";", taskData));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addNewTask(ActionEvent e) {
        String text = newTask.getText();
        LocalDate deadline = deadlinePicker.getValue();
        if (!text.isEmpty()) {
            taskList.getItems().add(new Task(text, deadline));
            newTask.setText("");
            deadlinePicker.setValue(null);
            sortTasks();
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
        saveDataToFile();
        System.exit(0);
    }

    private void saveDataToFile() {
        ObservableList<Task> tasks = taskList.getItems();
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Task task : tasks) {
                writer.write(task.getDescription() + ";" +
                        (task.getDeadline() != null ? task.getDeadline().toString() : "null") + ";" +
                        (task.getTime() != null ? task.getTime().toString() : "null") + ";" +
                        task.isCompleted() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sortTasks() {
        String selectedOption = sortChoiceBox.getValue();
        if (selectedOption == null) {
            return;
        }
        ObservableList<Task> tasks = taskList.getItems();
        Comparator<Task> comparator;
        switch (selectedOption) {
            case "Sort by Date":
                comparator = Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Task::getDescription, String::compareToIgnoreCase);
                break;
            case "Sort by Task Name":
                comparator = Comparator.comparing(Task::getDescription, String::compareToIgnoreCase)
                        .thenComparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            default:
                return;
        }
        FXCollections.sort(tasks, comparator);
    }
}
