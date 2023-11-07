package com.nolte.beerolympics2024;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.Arrays;
import java.util.List;

public class LeaderboardScreen {

    private List<Team> teams;
    private Stage previousStage;

    public LeaderboardScreen(List<Team> teams, Stage previousStage) {
        this.teams = teams;
        this.previousStage = previousStage;
    }

    public void display() {
        Platform.runLater(() -> {
            VBox mainVBox = new VBox(10);
            mainVBox.setAlignment(Pos.TOP_CENTER);
            mainVBox.setFillWidth(true);
            mainVBox.setStyle("-fx-background-color: #657ED4;");
            mainVBox.setPadding(new Insets(20, 0, 20, 0)); // Add padding to the top and bottom

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(mainVBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            Scene scene = new Scene(scrollPane);
            Stage stage = new Stage();
            stage.setMaximized(true);

            // Use percentage widths for responsive column sizes
            double[] columnPercentages = {15, 40, 15, 10, 10, 10}; // Sum should be 100%
            ColumnConstraints[] columnConstraints = Arrays.stream(columnPercentages)
                    .mapToObj(percentage -> {
                        ColumnConstraints constraints = new ColumnConstraints();
                        constraints.setPercentWidth(percentage);
                        constraints.setHgrow(Priority.ALWAYS);
                        constraints.setHalignment(HPos.CENTER);
                        return constraints;
                    }).toArray(ColumnConstraints[]::new);

            // Header labels
            GridPane headerGrid = new GridPane();
            headerGrid.setAlignment(Pos.CENTER);
            headerGrid.setPadding(new Insets(10, 50, 10, 50));
            headerGrid.getColumnConstraints().addAll(columnConstraints);

            // Add the header labels
            headerGrid.add(createStyledHeaderLabel("Team Name"), 0, 0);
            headerGrid.add(createStyledHeaderLabel("Athletes"), 1, 0);
            headerGrid.add(createStyledHeaderLabel("Total Score"), 2, 0);
            headerGrid.add(createStyledHeaderLabel("Game Points"), 3, 0);
            headerGrid.add(createStyledHeaderLabel("Drinks Remaining"), 4, 0);
            headerGrid.add(createStyledHeaderLabel("Pukes"), 5, 0);

            mainVBox.getChildren().add(headerGrid);

            int rowCounter = 0;
            // Teams and their information
            for (Team team : teams) {
                GridPane teamGrid = new GridPane();
                teamGrid.setAlignment(Pos.CENTER);
                teamGrid.setHgap(10);
                teamGrid.setPadding(new Insets(10, 50, 10, 50));
                teamGrid.getColumnConstraints().addAll(columnConstraints);

                Rectangle rect = new Rectangle(0, 100);
                rect.setArcWidth(30);
                rect.setArcHeight(30);
                if(rowCounter == 0) {
                    rect.setFill(Color.web("#FFD700"));
                    rect.setStroke(Color.web("#DAA520"));
                    rect.setStrokeWidth(7.0);
                }
                else if(rowCounter == 1)
                    rect.setFill(Color.web("#C0C0C0"));
                else if(rowCounter == 2)
                    rect.setFill(Color.web("#CD7F32"));
                else
                    rect.setFill(Color.web("#3626A7"));

                rect.widthProperty().bind(Bindings.min(scene.widthProperty().subtract(100), mainVBox.widthProperty()));

                Text teamName;
                teamName = createStyledText(team.getTeamName(), "#fbfbff", 24);
                if(rowCounter == 0)
                    teamName = createStyledText(team.getTeamName(), "#fbfbff", 24, "#0D0106");
                teamGrid.add(teamName, 0, 0);

                // Create the HBox for contestants
                HBox contestantsHBox;
                if(rowCounter == 0)
                    contestantsHBox = createContestantsHBox(team, true);
                else
                    contestantsHBox = createContestantsHBox(team);
                GridPane.setHalignment(contestantsHBox, HPos.CENTER); // This line ensures the HBox itself is centered in its cell
                GridPane.setValignment(contestantsHBox, VPos.CENTER); // This line ensures the HBox itself is vertically centered
                teamGrid.add(contestantsHBox, 1, 0); // Ensure this is the correct column index for athletes

                if(rowCounter!=0)
                    addTeamScoresToGridPane(teamGrid, team);
                else
                    addTeamScoresToGridPane(teamGrid, team,true);

                rowCounter++;

                mainVBox.setSpacing(scene.getHeight() / 20);

                StackPane stackPane = new StackPane();
                stackPane.setAlignment(Pos.CENTER);
                stackPane.getChildren().addAll(rect, teamGrid);

                // Optionally, add padding to each row if you want even more space between them
                stackPane.setPadding(new Insets(10, 0, 10, 0)); // Add padding to the top and bottom of each row

                mainVBox.getChildren().add(stackPane);
            }

            // Create an HBox for the image and button
            HBox bottomHBox = new HBox(20);
            bottomHBox.setAlignment(Pos.CENTER);

            // Add the "Game Screen" button
            Button gameScreenButton = new Button("Game Screen");
            gameScreenButton.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 24));
            // Add your styling for the button here
            gameScreenButton.setOnAction(event -> {
                // Handle the button click to go to the Game Screen
                // You will replace this with your actual navigation logic
                System.out.println("Navigate to the Game Screen");
            });

            // Add the image and button to the HBox
            bottomHBox.getChildren().addAll(gameScreenButton);

            // Add a spacer Pane to push the HBox to the bottom
            Pane spacer = new Pane();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            mainVBox.getChildren().addAll(spacer, bottomHBox);

            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        });
    }

    private HBox createContestantsHBox(Team team) {
        HBox contestantsHBox = new HBox(10);
        contestantsHBox.setAlignment(Pos.CENTER); // This centers the contents of the HBox itself

        for (Contestant contestant : team.getContestants()) {
            VBox contestantBox = new VBox(5);
            contestantBox.setAlignment(Pos.CENTER); // This centers the content of each contestant's VBox

            Image image = new Image(contestant.getImagePath());
            Circle circle = new Circle(30);
            circle.setFill(new ImagePattern(image));

            Text nameLabel = new Text(contestant.getName());
            nameLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14));
            nameLabel.setFill(Color.web("#fbfbff")); // Font color as per your original code

            contestantBox.getChildren().addAll(circle, nameLabel);
            contestantsHBox.getChildren().add(contestantBox);
        }
        return contestantsHBox;
    }

    private HBox createContestantsHBox(Team team, boolean gold) {
        HBox contestantsHBox = new HBox(10);
        contestantsHBox.setAlignment(Pos.CENTER); // This centers the contents of the HBox itself

        for (Contestant contestant : team.getContestants()) {
            VBox contestantBox = new VBox(5);
            contestantBox.setAlignment(Pos.CENTER); // This centers the content of each contestant's VBox

            Image image = new Image(contestant.getImagePath());
            Circle circle = new Circle(30);
            circle.setFill(new ImagePattern(image));

            Text nameLabel = new Text(contestant.getName());
            nameLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 14));
            nameLabel.setFill(Color.web("#fbfbff")); // Font color as per your original code
            nameLabel.setStrokeWidth(0.2);
            nameLabel.setFill(Color.web("#0D0106"));

            contestantBox.getChildren().addAll(circle, nameLabel);
            contestantsHBox.getChildren().add(contestantBox);
        }
        return contestantsHBox;
    }

    private Text createStyledHeaderLabel(String text) {
        Text headerLabel = new Text(text);
        headerLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 20));
        headerLabel.setFill(Color.web("#fbfbff")); // Font color as per your original code
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setValignment(headerLabel, VPos.CENTER);
        return headerLabel;
    }

    private Text createStyledText(String content, String color, int fontSize) {
        Text text = new Text(content);
        text.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, fontSize));
        text.setFill(Color.web(color));
        return text;
    }

    private Text createStyledText(String content, String color, int fontSize, String strokeColor) {
        fontSize *= 1.25;
        Text text = new Text(content);
        text.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, fontSize));
        text.setStroke(Color.web(strokeColor));
        text.setStrokeWidth((double) fontSize/33);
        text.setFill(Color.web(color));
        return text;
    }

    private void addTeamScoresToGridPane(GridPane gridPane, Team team) {
        Text scoreValue = createStyledText(String.valueOf(team.getScore()), "#fbfbff", 30);
        Text pointsValue = createStyledText(String.valueOf(team.getPoints()), "#fbfbff", 30);
        Text drinksValue = createStyledText(String.valueOf(team.getDrinks()), "#fbfbff", 30);
        Text pukesValue = createStyledText(String.valueOf(team.getPukes()), "#fbfbff", 30);

        gridPane.add(scoreValue, 2, 0);
        gridPane.add(pointsValue, 2 + 1, 0);
        gridPane.add(drinksValue, 2 + 2, 0);
        gridPane.add(pukesValue, 2 + 3, 0);
    }

    private void addTeamScoresToGridPane(GridPane gridPane, Team team, boolean gold) {
        Text scoreValue = createStyledText(String.valueOf(team.getScore()), "#fbfbff", 30, "0d0106");
        Text pointsValue = createStyledText(String.valueOf(team.getPoints()), "#fbfbff", 30, "0d0106");
        Text drinksValue = createStyledText(String.valueOf(team.getDrinks()), "#fbfbff", 30, "0d0106");
        Text pukesValue = createStyledText(String.valueOf(team.getPukes()), "#fbfbff", 30, "0d0106");

        gridPane.add(scoreValue, 2, 0);
        gridPane.add(pointsValue, 2 + 1, 0);
        gridPane.add(drinksValue, 2 + 2, 0);
        gridPane.add(pukesValue, 2 + 3, 0);
    }
}
