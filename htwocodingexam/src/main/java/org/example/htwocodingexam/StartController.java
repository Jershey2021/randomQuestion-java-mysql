package org.example.htwocodingexam;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class StartController {


    @FXML
    private Button startCloseBtn;

    @FXML
    private Button startQuizBtn;

    @FXML
    private void initialize()
    {
        startCloseBtn.setOnAction(e -> System.exit(0));

        startQuizBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try
                {
                    Stage thisStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    thisStage.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("question.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    scene.setFill(Color.TRANSPARENT);
                    stage.show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}