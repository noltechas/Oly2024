package com.nolte.beerolympics2024;

import com.nolte.beerolympics2024.Contestant;
import com.nolte.beerolympics2024.Team;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.util.List;

public class LeaderboardScreen {

    private List<Team> teams;

    // Constructor
    private Stage previousStage; // Add a reference to the previous stage

    // Updated constructor to accept the previous stage
    public LeaderboardScreen(List<Team> teams, Stage previousStage) {
        this.teams = teams;
        this.previousStage = previousStage; // Store the reference to the previous stage
    }

    // Method to display the screen
    public void display() {
        Platform.runLater(() -> {
            VBox mainVBox = new VBox(10); // Spacing between rows
            mainVBox.setAlignment(Pos.TOP_CENTER);
            mainVBox.setFillWidth(true);
            mainVBox.setStyle("-fx-background-color: #053C5E;");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(mainVBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            Scene scene = new Scene(scrollPane);
            Stage stage = new Stage();
            stage.setMaximized(true);

            // Define column constraints for team name and contestants
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setHgrow(Priority.ALWAYS); // Allow this column to grow and fill space
            column1.setHalignment(HPos.CENTER); // Center align the team name

            ColumnConstraints column2 = new ColumnConstraints();
            column2.setHgrow(Priority.ALWAYS); // Allow this column to grow and fill space
            column2.setHalignment(HPos.CENTER); // Center align the contestants

            // Define column constraints for the new labels
            ColumnConstraints scoreColumn = new ColumnConstraints();
            scoreColumn.setHalignment(HPos.CENTER);
            scoreColumn.setHgrow(Priority.ALWAYS);

            ColumnConstraints pointsColumn = new ColumnConstraints();
            pointsColumn.setHalignment(HPos.CENTER);
            pointsColumn.setHgrow(Priority.ALWAYS);

            ColumnConstraints drinksColumn = new ColumnConstraints();
            drinksColumn.setHalignment(HPos.CENTER);
            drinksColumn.setHgrow(Priority.ALWAYS);

            ColumnConstraints pukesColumn = new ColumnConstraints();
            pukesColumn.setHalignment(HPos.CENTER);
            pukesColumn.setHgrow(Priority.ALWAYS);

            // Add header labels for the new columns to the labelsGridPane
            Text scoreLabel = new Text("Total Score");
            scoreLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            Text pointsLabel = new Text("Game Points");
            pointsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            Text drinksLabel = new Text("Drinks Remaining");
            drinksLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            Text pukesLabel = new Text("Pukes");
            pukesLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            // Row labels in a grid pane with similar constraints
            GridPane labelsGridPane = new GridPane();
            labelsGridPane.setAlignment(Pos.CENTER);
            labelsGridPane.getColumnConstraints().addAll(column1, column2);

            Text teamLabel1 = new Text("Team Name");
            teamLabel1.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            Text athletesLabel1 = new Text("Athletes");
            athletesLabel1.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            // Add the labels to the grid pane and center them
            GridPane.setConstraints(teamLabel1, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER);
            GridPane.setConstraints(athletesLabel1, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
            labelsGridPane.add(teamLabel1, 0, 0);
            labelsGridPane.add(athletesLabel1, 1, 0);
            labelsGridPane.add(scoreLabel, 2, 0);
            labelsGridPane.add(pointsLabel, 3, 0);
            labelsGridPane.add(drinksLabel, 4, 0);
            labelsGridPane.add(pukesLabel, 5, 0);

            mainVBox.getChildren().add(labelsGridPane); // Add the labels grid pane to the main VBox

            for (Team team : teams) {
                GridPane gridPane = new GridPane();
                gridPane.setAlignment(Pos.CENTER);
                gridPane.setHgap(10);
                gridPane.setPadding(new Insets(10, 50, 10, 50));
                gridPane.getColumnConstraints().addAll(column1, column2, scoreColumn, pointsColumn, drinksColumn, pukesColumn);

                Rectangle rect = new Rectangle(0, 100);
                rect.setArcWidth(30);
                rect.setArcHeight(30);
                rect.setFill(Color.web("#BFDBF7"));
                rect.widthProperty().bind(scene.widthProperty().subtract(100));

                Text teamName = new Text(team.getTeamName());
                teamName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #A31621;");

                GridPane.setHalignment(teamName, HPos.CENTER);
                GridPane.setValignment(teamName, VPos.CENTER);

                HBox contestantsHBox = new HBox(10);
                contestantsHBox.setAlignment(Pos.CENTER); // Center the contents of the HBox

                gridPane.add(teamName, 0, 0);
                gridPane.add(contestantsHBox, 1, 0);

                // Add the new columns with the team values
                Text scoreValue = new Text(String.valueOf(team.getScore()));
                scoreValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #1f7a8c;");
                Text pointsValue = new Text(String.valueOf(team.getPoints()));
                pointsValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #1f7a8c;");
                Text drinksValue = new Text(String.valueOf(team.getDrinks()));
                drinksValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #1f7a8c;");
                Text pukesValue = new Text(String.valueOf(team.getPukes()));
                pukesValue.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #1f7a8c;");

                // Add these Text objects to the gridPane
                gridPane.add(scoreValue, 2, 0);
                gridPane.add(pointsValue, 3, 0);
                gridPane.add(drinksValue, 4, 0);
                gridPane.add(pukesValue, 5, 0);

                for (Contestant contestant : team.getContestants()) {
                    VBox contestantBox = new VBox(5);
                    contestantBox.setAlignment(Pos.CENTER);

                    Image image = new Image(contestant.getImagePath());
                    Circle circle = new Circle(30);
                    circle.setFill(new ImagePattern(image));

                    Label nameLabel = new Label(contestant.getName());
                    nameLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14));
                    nameLabel.setTextFill(Color.web("#053C5E"));

                    contestantBox.getChildren().addAll(circle, nameLabel);
                    contestantsHBox.getChildren().add(contestantBox);
                }

                StackPane stackPane = new StackPane();
                StackPane.setAlignment(rect, Pos.CENTER);
                StackPane.setAlignment(gridPane, Pos.CENTER);
                stackPane.getChildren().addAll(rect, gridPane);
                mainVBox.getChildren().add(stackPane);
            }

            stage.setScene(scene);
            stage.show();
        });
    }

}
