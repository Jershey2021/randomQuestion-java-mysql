module org.example.htwocodingexam {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.htwocodingexam to javafx.fxml;
    exports org.example.htwocodingexam;
}