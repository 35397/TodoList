module com.example.todolistjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.todolistjavafx to javafx.fxml;
    exports com.example.todolistjavafx;
}