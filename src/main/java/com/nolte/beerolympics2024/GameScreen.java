package com.nolte.beerolympics2024;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends Application {

    private List<Game> gamesList = new ArrayList<>();
    private SpinningWheel spinningWheel; // This is a custom class or control you would need to create
    private double initialWidth;
    private double initialHeight;

    public GameScreen(double initialWidth, double initialHeight) {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;
    }
    private double wheelRadius = 150; // Example radius, adjust as needed

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadGamesFromJson("games.json"); // Load games from JSON file

        BorderPane root = new BorderPane();

        // Initialize Spinning Wheel with radius
        spinningWheel = new SpinningWheel(wheelRadius);
        Button spinButton = new Button("Spin");
        spinButton.setOnAction(e -> spinningWheel.spin());
        HBox topPane = new HBox(spinningWheel, spinButton);
        root.setTop(topPane);

        // Bottom - Games list
        GridPane gamesGrid = new GridPane();
        ScrollPane scrollPane = new ScrollPane(gamesGrid);
        root.setCenter(scrollPane);

        int row = 0, column = 0;
        Random colorRandom = new Random();
        for (Game game : gamesList) {
            CheckBox checkBox = new CheckBox(game.getName() + " - " + game.getNumOfPlayers());
            checkBox.setSelected(true);

            // Generate a random color for each game
            Color color = new Color(colorRandom.nextDouble(), colorRandom.nextDouble(), colorRandom.nextDouble(), 1);

            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    spinningWheel.addGame(game.getName(), color);
                } else {
                    spinningWheel.removeGame(game.getName());
                }
            });

            // Initially, add all games to the spinning wheel with a random color
            spinningWheel.addGame(game.getName(), color);

            gamesGrid.add(checkBox, column, row);
            column++;
            if (column > 1) {
                column = 0;
                row++;
            }
        }

        // Set the ScrollPane to appear if there are more than 6 games
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Scene scene = new Scene(root, initialWidth, initialHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadGamesFromJson(String jsonFilePath) {
        // Implement JSON loading logic
        JSONParser parser = new JSONParser();
        try {
            JSONArray gamesArray = (JSONArray) parser.parse(new FileReader(jsonFilePath));
            for (Object o : gamesArray) {
                JSONObject gameJson = (JSONObject) o;
                String name = (String) gameJson.get("name");
                long numOfPlayers = (long) gameJson.get("numOfPlayers");
                gamesList.add(new Game(name, (int) numOfPlayers));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}