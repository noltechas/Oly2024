package com.nolte.beerolympics2024;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.File;
import java.net.MalformedURLException;
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
            mainVBox.setPadding(new Insets(20, 0, 20, 0));

            // Instead of ScrollPane, directly use the VBox in the scene
            Scene scene = new Scene(mainVBox);
            Stage stage = new Stage();
            stage.setMaximized(true);

            // Add a listener to the height property of the scene
            scene.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                if (newHeight.doubleValue() > 0) {
                    double totalAvailableHeight = newHeight.doubleValue() * 0.65; // 90% of scene height
                    double rowHeight = totalAvailableHeight / teams.size(); // Height for each row
                    double spacing = 0; // 1% of scene height for spacing
                    mainVBox.setSpacing(spacing);

                    // Update existing elements or create new elements that depend on the scene's height
                    // For example, update the heights of rectangles or other components
                    for (Node child : mainVBox.getChildren()) {
                        if (child instanceof StackPane stackPane) {
                            Rectangle rect = (Rectangle) stackPane.getChildren().get(0);
                            rect.setHeight(rowHeight - spacing); // Update the height of the rectangle
                        }
                    }
                }
            });
            scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                if (newWidth.doubleValue() > 0) {
                    // Adjust column widths and other elements based on the new width
                    for (Node child : mainVBox.getChildren()) {
                        if (child instanceof StackPane stackPane) {
                            GridPane teamGrid = (GridPane) stackPane.getChildren().get(1);

                            // Update the width of rectangles or other components
                            Rectangle rect = (Rectangle) stackPane.getChildren().get(0);
                            rect.setWidth(scene.getWidth()*0.95); // Adjust 'someFixedMargin' as needed

                            // Update column constraints for the teamGrid based on newWidth
                            double[] columnPercentages = {15, 40, 15, 10, 10, 10}; // Example percentages
                            teamGrid.getColumnConstraints().clear();
                            for (double percentage : columnPercentages) {
                                ColumnConstraints constraints = new ColumnConstraints();
                                constraints.setPercentWidth(percentage);
                                teamGrid.getColumnConstraints().add(constraints);
                            }

                            // Adjust spacing in HBoxes if you have any
                            // Example: contestantsHBox.setSpacing(newWidth.doubleValue() * 0.01);
                        }
                    }
                }
            });
            mainVBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                            new Insets(scene.getHeight() * 0.02, 0, scene.getHeight() * 0.02, 0),
                    scene.heightProperty()));

            // Define the percentage widths for each column
            double[] columnPercentages = new double[] { 20, 31, 9, 18, 6, 16 }; // Adjust these percentages to match your layout

            // Create the header GridPane
            GridPane headerGrid = new GridPane();
            headerGrid.setAlignment(Pos.CENTER);
            headerGrid.setPadding(new Insets(10, 0, 10, 0));

            // Set the column constraints with percentages for the header grid
            for (int i = 0; i < columnPercentages.length; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setPercentWidth(columnPercentages[i]);
                cc.setHalignment(HPos.CENTER); // Center alignment for headers
                headerGrid.getColumnConstraints().add(cc);
            }

// Add the header labels to the GridPane
            String[] headers = new String[] { "Team Name", "Athletes", "Total Score", "Game Points", "Drinks", "Pukes" };
            for (int i = 0; i < headers.length; i++) {
                Text headerLabel = createStyledHeaderLabel(headers[i]);
                headerLabel.setTextAlignment(TextAlignment.CENTER);
                headerGrid.add(headerLabel, i, 0);
            }

            mainVBox.getChildren().add(0, headerGrid); // Add at the beginning (index 0)

// When creating each team's GridPane, set the same column constraints
            int rowCounter = 0;
            for (Team team : teams) {
                GridPane teamGrid = new GridPane();
                teamGrid.setAlignment(Pos.CENTER);
                teamGrid.setPadding(new Insets(10, 50, 10, 50)); // Adjust if needed

                // Set the same column constraints with percentages for the team grid
                for (double percentage : columnPercentages) {
                    ColumnConstraints cc = new ColumnConstraints();
                    cc.setPercentWidth(percentage);
                    teamGrid.getColumnConstraints().add(cc);
                }
                double totalAvailableHeight = scene.heightProperty().multiply(0.95).doubleValue(); // 90% of scene height
                double rowHeight = totalAvailableHeight / teams.size(); // Height for each row
                double spacing = scene.heightProperty().multiply(0.01).doubleValue(); // 1% of scene height for spacing

                Rectangle rect = new Rectangle(0, rowHeight - spacing); // Subtract spacing to account for gaps
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

                //rect.widthProperty().bind(Bindings.min(scene.widthProperty().subtract(100), mainVBox.widthProperty()));

                Text teamName;
                teamName = createStyledText(team.getTeamName(), "#fbfbff", 24);
                if(rowCounter == 0)
                    teamName = createStyledText(team.getTeamName(), "#fbfbff", 24, "#0D0106");
                teamGrid.add(teamName, 0, 0);

                // Create the HBox for contestants
                HBox contestantsHBox;
                if(rowCounter == 0) {
                    try {
                        contestantsHBox = createContestantsHBox(team, true);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
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

                mainVBox.setSpacing(spacing);

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

            Button gameScreenButton = new Button("Game Screen");
            gameScreenButton.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 24));
            gameScreenButton.setOnAction(event -> {
                GameScreen gameScreen = new GameScreen(stage.getWidth(), stage.getHeight());

                // Close the current stage if necessary
                stage.close();

                // Create a new stage for the GameScreen
                Stage gameStage = new Stage();
                try {
                    gameScreen.start(gameStage); // This will open the GameScreen with the specified dimensions
                } catch (Exception e) {
                    e.printStackTrace(); // Handle the exception appropriately
                }
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

            File file = new File(contestant.getImagePath());
            String imageURL = null;
            try {
                imageURL = file.toURI().toURL().toExternalForm();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            Image image = new Image(imageURL);
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

    private HBox createContestantsHBox(Team team, boolean gold) throws MalformedURLException {
        HBox contestantsHBox = new HBox(10);
        contestantsHBox.setAlignment(Pos.CENTER); // This centers the contents of the HBox itself

        for (Contestant contestant : team.getContestants()) {
            VBox contestantBox = new VBox(5);
            contestantBox.setAlignment(Pos.CENTER); // This centers the content of each contestant's VBox

            File file = new File(contestant.getImagePath());
            String imageURL = file.toURI().toURL().toExternalForm();
            Image image = new Image(imageURL);
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
