package org.example.htwocodingexam;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.EventObject;
import java.util.Optional;

public class ResultsController {

    @FXML
    private Label totalAnswerA;

    @FXML
    private Label totalAnswerB;

    @FXML
    private Label totalAnswerC;

    @FXML
    private Label totalAnswer;

    @FXML
    private Label finalResults;

    @FXML
    private Button confirmButton;

    private int[] answerCounts;

    public void initialize(){
        confirmButton.setOnAction(e -> handleConfirmButton());
    }

    public void setAnswerCounts(int[] answerCounts) {
        this.answerCounts = answerCounts;
        updateResults();
    }

    private void updateResults() {
        if (answerCounts != null) {
            totalAnswerA.setText(String.valueOf(answerCounts[0]));
            totalAnswerB.setText(String.valueOf(answerCounts[1]));
            totalAnswerC.setText(String.valueOf(answerCounts[2]));
            totalAnswer.setText(String.valueOf(answerCounts[0] + answerCounts[1] + answerCounts[2]));

            String resultMessage = fetchResultMessage(answerCounts[0], answerCounts[1], answerCounts[2]);
            finalResults.setText(resultMessage);

        }
    }

    private String fetchResultMessage(int countA, int countB, int countC) {
        String message = "Equal Skills: You have balanced skills in self-management and teamwork.";
        String url = "jdbc:mysql://localhost:3306/randomquestion";
        String user = "admin";
        String password = "admin123";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql;

            if (countA > countB && countA > countC)
            {
                sql = "SELECT results FROM result WHERE resultnumber = 2";
            }
            else if (countB > countA && countB > countC)
            {
                sql = "SELECT results FROM result WHERE resultnumber = 3";
            }
            else if (countC > countA && countC > countB)
            {
                sql = "SELECT results FROM result WHERE resultnumber = 1";
            }
            else if (countA == countB && countB == countA && countA > countC && countB > countC)
            {
                sql = "SELECT results FROM result WHERE resultnumber = 3";
            }
            else if (countA == countC && countC == countA && countA > countB && countC > countB)
            {
                sql = "SELECT results FROM result WHERE resultnumber = 2";
            }
            else if (countB == countC && countC == countB && countB > countA && countC > countA)
            {
                sql = "SELECT results FROM result WHERE resultnumber = 3";
            }
            else
            {
                sql = "SELECT results FROM result WHERE resultnumber = 3";
            }

            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                message = resultSet.getString("results");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }


    private void handleConfirmButton(){
        showCongratulationsAlert();
    }

    private void showCongratulationsAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("Konnichiwa" + " " + "You're Great!");
        alert.setContentText("HAVE A GREAT DAY!");

        ButtonType closeButton = new ButtonType("Close");
        ButtonType tryAgainButton = new ButtonType("Try Again");

        alert.getButtonTypes().setAll(tryAgainButton, closeButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == closeButton) {
                Stage stage = (Stage) confirmButton.getScene().getWindow();
                stage.close();
            } else if (result.get() == tryAgainButton) {
                try {

                    Stage currentStage = (Stage) confirmButton.getScene().getWindow();
                    currentStage.close();

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("start.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initStyle(StageStyle.TRANSPARENT);
                    scene.setFill(Color.TRANSPARENT);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace(); // Handle exception
                }
            }
        }
    }
}
