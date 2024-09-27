package org.example.htwocodingexam;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;

import javafx.stage.Stage;


import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionController {

    @FXML
    private Label questionLabel;

    @FXML
    private RadioButton optionA;

    @FXML
    private RadioButton optionB;

    @FXML
    private RadioButton optionC;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button doneButton;

    private List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private static final int MAX_QUESTIONS = 8;
    private int[] answerCounts = new int[3]; // 0: A, 1: B, 2: C
    private Integer[] selectedAnswers;

    public void initialize() {
        fetchQuestions();
        selectedAnswers = new Integer[MAX_QUESTIONS];
        displayCurrentQuestion();
        ToggleGroup toggleGroup = new ToggleGroup();
        optionA.setToggleGroup(toggleGroup);
        optionB.setToggleGroup(toggleGroup);
        optionC.setToggleGroup(toggleGroup);

        nextButton.setOnAction(e -> handleNextButton());
        prevButton.setOnAction(e -> handlePrevButton());
        doneButton.setOnAction(e -> handleDoneButton());
    }

    private void fetchQuestions() {
        String url = "jdbc:mysql://localhost:3306/randomquestion";
        String user = "admin";
        String password = "admin123";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questions LIMIT 8")) {

            while (rs.next()) {
                questions.add(new Question(rs.getInt("id"), rs.getString("ranquestion"),
                        rs.getString("a"), rs.getString("b"), rs.getString("c")));
            }
            Collections.shuffle(questions); // Shuffle the list
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayCurrentQuestion() {
        if (currentIndex >= 0 && currentIndex < questions.size()) {
            Question question = questions.get(currentIndex);
            questionLabel.setText(question.getQuestionText());
            optionA.setText(question.getOptionA());
            optionB.setText(question.getOptionB());
            optionC.setText(question.getOptionC());
            updateButtonStates();
            updateFinishButtonVisibility();

            if (selectedAnswers[currentIndex] != null) {
                switch (selectedAnswers[currentIndex]) {
                    case 0 -> optionA.setSelected(true);
                    case 1 -> optionB.setSelected(true);
                    case 2 -> optionC.setSelected(true);

                }
            } else {
                optionA.setSelected(false);
                optionB.setSelected(false);
                optionC.setSelected(false);
            }

        }
    }

    private void updateButtonStates() {
        prevButton.setDisable(currentIndex == 0); // Disable prev button if at the first question
        nextButton.setDisable(currentIndex >= MAX_QUESTIONS - 1); // Disable next button after max questions
    }

    private void updateFinishButtonVisibility() {
        doneButton.setVisible(currentIndex == MAX_QUESTIONS - 1); // Show only on the last question
    }

    @FXML
    private void handleNextButton() {
        recordAnswer();
        if (currentIndex < MAX_QUESTIONS - 1) {
            currentIndex++;
            displayCurrentQuestion();
        } else {
            handleDoneButton();
        }
    }

    @FXML
    private void handlePrevButton() {
        recordAnswer();
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrentQuestion();
        }
    }

    private void handleDoneButton() {
        recordAnswer();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("results.fxml"));
            Stage stage = (Stage) doneButton.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());


            // Get the controller for the results view and set the answer counts
            ResultsController resultsController = fxmlLoader.getController();
            resultsController.setAnswerCounts(answerCounts);

            stage.setScene(scene);
            stage.setTitle("Results");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordAnswer() {
        ToggleGroup group = optionA.getToggleGroup();
        if (group.getSelectedToggle() != null) {
            int selectedOption = -1;
            if (optionA.isSelected()) selectedOption = 0;
            else if (optionB.isSelected()) selectedOption = 1;
            else if (optionC.isSelected()) selectedOption = 2;

            if (selectedAnswers[currentIndex] == null) {
                selectedAnswers[currentIndex] = selectedOption; // Record the selected option
                if (selectedOption == 0) answerCounts[0]++;
                else if (selectedOption == 1) answerCounts[1]++;
                else if (selectedOption == 2) answerCounts[2]++;
            } else if (!selectedAnswers[currentIndex].equals(selectedOption)) {
                // Decrease the count for the previously selected option
                int previousOption = selectedAnswers[currentIndex];
                if (previousOption != selectedOption) {
                    answerCounts[previousOption]--;
                }
                // Update to the new selection
                selectedAnswers[currentIndex] = selectedOption;
                if (selectedOption == 0) answerCounts[0]++;
                else if (selectedOption == 1) answerCounts[1]++;
                else if (selectedOption == 2) answerCounts[2]++;
            }
        }
    }
        private static class Question {
            private final int id;
            private final String questionText;
            private final String optionA;
            private final String optionB;
            private final String optionC;

            public Question(int id, String questionText, String optionA, String optionB, String optionC) {
                this.id = id;
                this.questionText = questionText;
                this.optionA = optionA;
                this.optionB = optionB;
                this.optionC = optionC;
            }

            public String getQuestionText() {
                return questionText;
            }

            public String getOptionA() {
                return optionA;
            }

            public String getOptionB() {
                return optionB;
            }

            public String getOptionC() {
                return optionC;
            }
        }
    }

