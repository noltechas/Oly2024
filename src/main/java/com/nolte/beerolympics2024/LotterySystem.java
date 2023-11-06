package com.nolte.beerolympics2024;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;
import javafx.embed.swing.SwingFXUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class LotterySystem extends Application {

    private int rankingCounter = 1;
    private static final String FOREGROUND_COLOR = "#89CFF0"; // Light blue
    private static final String TEXT_COLOR_1 = "#000000";     // Black
    private Image selectedImage = null;
    private ImageView previewImageView = new ImageView();
    private final Popup dragPopup = new Popup();
    private List<Object> rankings = new ArrayList<>();  // Initialize it as an empty list
    private static final String IMAGES_DIR = "contestant_images";  // New constant for image directory
    private VBox rankingsBox = new VBox(10);
    private ArrayList<Contestant> contestants;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadRankings();
        VBox leftPane = new VBox(20);
        leftPane.setPadding(new Insets(20));
        leftPane.setStyle("-fx-background-color: #3a4ed5; -fx-border-radius: 15; -fx-background-radius: 15;");
        leftPane.setMinWidth(300);  // Increased width
        leftPane.setPrefHeight(500);  // Added preferred height

        TextField nameField = new TextField();
        nameField.setPromptText("Name...");
        nameField.setFont(Font.font("Arial Rounded MT Bold", 16));
        nameField.setStyle("-fx-text-fill: " + TEXT_COLOR_1 + ";");

        Button startLotteryButton = new Button("Start Lottery");
        startLotteryButton.setFont(Font.font("Arial Rounded MT Bold", 16));
        startLotteryButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: " + TEXT_COLOR_1 + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15;");

        startLotteryButton.setOnAction(e -> {
            createContestants();
            Stage currentStage = (Stage) startLotteryButton.getScene().getWindow();
            NEWLotteryScreen lotteryScreen = new NEWLotteryScreen(contestants);
            lotteryScreen.initializeScreen(currentStage);
        });

        Button saveButton = new Button("Save");  // Added save button
        saveButton.setFont(Font.font("Arial Rounded MT Bold", 16));
        saveButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: " + TEXT_COLOR_1 + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15;");
        saveButton.setOnAction(e -> saveRankings());
        StackPane.setAlignment(saveButton, Pos.BOTTOM_RIGHT);

        Button selectButton = new Button("Select...");
        selectButton.setFont(Font.font("Arial Rounded MT Bold", 16));
        selectButton.setStyle("-fx-text-fill: " + TEXT_COLOR_1 + ";");

        ComboBox<String> choiceDropdown = new ComboBox<>();
        choiceDropdown.getItems().addAll("Choice A", "Choice B", "Choice C");
        choiceDropdown.setStyle("-fx-font: 16 'Arial Rounded MT Bold'; -fx-text-fill: " + FOREGROUND_COLOR + ";");

        Button addContestantButton = new Button("Add Contestant");
        addContestantButton.setFont(Font.font("Arial Rounded MT Bold", 16));
        addContestantButton.setStyle("-fx-text-fill: " + TEXT_COLOR_1 + ";");

        selectButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a Picture");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedImage = new Image(file.toURI().toString());
                previewImageView.setImage(selectedImage);
                previewImageView.setFitHeight(40);  // Set a size for the preview
                previewImageView.setFitWidth(40);
                previewImageView.setPreserveRatio(true);
            }
        });

        // Header Labels for Name, Picture, and Choice
        Label nameLabel = new Label("Name");
        Label pictureLabel = new Label("Picture");
        Label choiceLabel = new Label("Choice");

        // Setting font and style for the labels
        Font headerFont = Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 18);
        String headerStyle = "-fx-text-fill: " + FOREGROUND_COLOR + ";";
        nameLabel.setFont(headerFont);
        nameLabel.setStyle(headerStyle);
        pictureLabel.setFont(headerFont);
        pictureLabel.setStyle(headerStyle);
        choiceLabel.setFont(headerFont);
        choiceLabel.setStyle(headerStyle);

        HBox pictureSelectionBox = new HBox(10, pictureLabel, selectButton, previewImageView);
        HBox buttonsBox = new HBox(10, startLotteryButton, saveButton);
        buttonsBox.setAlignment(Pos.CENTER);
        leftPane.getChildren().addAll(nameLabel, nameField, pictureSelectionBox, choiceLabel, choiceDropdown, addContestantButton, buttonsBox);

        VBox rightPane = new VBox(20);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.setPadding(new Insets(20));
        rightPane.setStyle("-fx-background-color: #3a4ed5; -fx-border-radius: 15; -fx-background-radius: 15;");
        rightPane.setMinWidth(500);  // Increased width
        rightPane.setPrefHeight(800);  // Added preferred height

        VBox.setVgrow(rankingsBox, Priority.ALWAYS);
        rankingsBox.setOnDragOver(event -> {
            if (event.getGestureSource() != rankingsBox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        rankingsBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                int draggedIdx = Integer.parseInt(db.getString());
                Node draggedNode = rankingsBox.getChildren().get(draggedIdx);
                rankingsBox.getChildren().remove(draggedIdx);

                double targetY = event.getY();
                int targetIndex = 0;
                for (Node child : rankingsBox.getChildren()) {
                    if (child.getBoundsInParent().getMinY() > targetY) {
                        break;
                    }
                    targetIndex++;
                }
                rankingsBox.getChildren().add(targetIndex, draggedNode);
                success = true;
            }
            updateRankings(rankingsBox);
            event.setDropCompleted(success);
            event.consume();
        });

        ScrollPane rankingsScrollPane = new ScrollPane(rankingsBox);
        rankingsScrollPane.setStyle("-fx-background-color: #3a4ed5; -fx-border-color: #3a4ed5; -fx-scrollbar-arrow-increment-button: none; -fx-scrollbar-arrow-decrement-button: none;");
        rankingsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hide horizontal scrollbar.
        rankingsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show vertical scrollbar only when needed.
        rankingsScrollPane.setFitToWidth(true); // This ensures the width of the content matches the width of the scroll pane.

        // Use Platform.runLater to delay the styling until after initial rendering
        Platform.runLater(() -> {
            rankingsScrollPane.lookup(".viewport").setStyle("-fx-background-color: #3a4ed5;");
            rankingsScrollPane.lookup(".thumb").setStyle("-fx-background-color: #FFA500;"); // Change #FFA500 to your desired color.
            rankingsScrollPane.lookup(".track").setStyle("-fx-background-color: #6a57a9;"); // Change #6a57a9 to your desired color.
            rankingsScrollPane.setStyle("-fx-background-color: #3a4ed5; -fx-border-color: #3a4ed5; -fx-scrollbar-arrow-increment-button: none; -fx-scrollbar-arrow-decrement-button: none;");
        });

        // Setting label font styles for Rankings
        Label rankingsLabel = new Label("Rankings");
        rankingsLabel.setFont(headerFont);
        rankingsLabel.setStyle(headerStyle);
        rightPane.getChildren().addAll(rankingsLabel, rankingsScrollPane);

        HBox root = new HBox(100, leftPane, rightPane);
        root.setPadding(new Insets(40));
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: #2a3a64;");

        StackPane layout = new StackPane(root);
        StackPane.setAlignment(saveButton, Pos.TOP_RIGHT);

        // Setting label font styles
        for (Label label : new Label[]{(Label) leftPane.getChildren().get(0), (Label) rightPane.getChildren().get(0)}) {
            label.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 18));
            label.setStyle("-fx-text-fill: " + FOREGROUND_COLOR + ";");
        }

        String buttonStyle = "-fx-background-color: " + FOREGROUND_COLOR + "; -fx-text-fill: " + TEXT_COLOR_1 + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15;";
        selectButton.setStyle(buttonStyle);
        addContestantButton.setStyle(buttonStyle);
        addContestantButton.setOnAction(e -> {
            if (!nameField.getText().isEmpty() && choiceDropdown.getValue() != null) {
                HBox rankEntry = new HBox(10);
                rankEntry.setSpacing(20);
                rankEntry.setPadding(new Insets(10));
                rankEntry.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                Label rankNumber = new Label(String.valueOf(rankingCounter++));
                rankNumber.setStyle("-fx-text-fill: yellow; -fx-font-size: 24; -fx-font-family: 'Arial Rounded MT Bold';");

                Label playerName = new Label(nameField.getText() + " | " + choiceDropdown.getValue());
                playerName.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 18));
                playerName.setStyle("-fx-text-fill: " + FOREGROUND_COLOR + "; -fx-background-color: #6a57a9; -fx-border-radius: 15; -fx-padding: 5 15; -fx-background-radius: 15;");

                // If an image was selected, display it in a circle next to the name
                ImageView playerImageView = null;
                if (selectedImage != null) {
                    playerImageView = new ImageView(selectedImage);
                    playerImageView.setFitHeight(40);
                    playerImageView.setFitWidth(40);
                    playerImageView.setClip(new Circle(20, 20, 20));
                    playerName.setGraphic(playerImageView);
                    playerName.setContentDisplay(ContentDisplay.LEFT);
                }

                HBox playerInfoBox = new HBox(10, playerImageView, playerName) {
                    // Store initial position
                    private double initialY;

                    {
                        setOnDragDetected(event -> {
                            Dragboard db = startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent content = new ClipboardContent();

                            // Adjusting the SnapshotParameters for transparency
                            SnapshotParameters snapshotParameters = new SnapshotParameters();
                            snapshotParameters.setFill(javafx.scene.paint.Color.TRANSPARENT);

                            // Capture a snapshot of the playerInfoBox node
                            WritableImage snapshot = snapshot(snapshotParameters, null);

                            // Set the drag view for the Dragboard
                            db.setDragView(snapshot, event.getX(), event.getY());

                            content.putString(String.valueOf(rankingsBox.getChildren().indexOf(getParent())));
                            db.setContent(content);
                            event.consume();
                        });

                    }
                };

                playerInfoBox.setId("player-" + rankingCounter);  // Set unique ID for each playerInfoBox
                playerInfoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                playerInfoBox.setStyle("-fx-background-color: #6a57a9; -fx-border-radius: 15; -fx-background-radius: 15; -fx-padding: 5 15;");

                rankEntry.getChildren().addAll(rankNumber, playerInfoBox);
                rankingsBox.getChildren().add(rankEntry);

                // Save image to directory
                String imagePath = null;
                if (selectedImage != null) {
                    File imageDir = new File(IMAGES_DIR);
                    if (!imageDir.exists()) {
                        imageDir.mkdir();
                    }
                    String fileName = System.currentTimeMillis() + ".png";
                    File outputFile = new File(imageDir, fileName);
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(selectedImage, null), "png", outputFile);
                        imagePath = outputFile.getAbsolutePath();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

                // Add contestant details to the rankings list
                JSONObject contestantDetails = new JSONObject();
                contestantDetails.put("rank", rankingCounter - 1);
                contestantDetails.put("name", nameField.getText());
                contestantDetails.put("choice", choiceDropdown.getValue());
                contestantDetails.put("imagePath", imagePath);
                rankings.add(contestantDetails);

                nameField.clear();
                choiceDropdown.setValue(null);
                selectedImage = null;  // Reset the selected image after adding contestant
            }
        });

        VBox mainLayout = new VBox(20, root, startLotteryButton); // Stack the main content and the button vertically
        mainLayout.setAlignment(javafx.geometry.Pos.CENTER);
        mainLayout.setPadding(new Insets(10, 40, 40, 40));
        mainLayout.setStyle("-fx-background-color: #2a3a64;");

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Lottery System");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1700);  // Example width value
        primaryStage.setHeight(850);   // Example height value
        primaryStage.show();
    }

    private void createContestants() {
        this.contestants = new ArrayList<>();
        for (int i = 0; i < rankings.size(); i++) {
            JSONObject contestant = (JSONObject) rankings.get(i); // Cast to JSONObject
            contestants.add(new Contestant(contestant.get("name").toString(), Integer.parseInt(String.valueOf(contestant.get("rank"))), contestant.get("choice").toString(), contestant.get("imagePath").toString()));
        }
    }

    private void updateRankings(VBox rankingsBox) {
        int newRanking = 1;
        for (Node node : rankingsBox.getChildren()) {
            if (node instanceof HBox) {
                Label rankNumber = (Label) ((HBox) node).getChildren().get(0);
                rankNumber.setText(String.valueOf(newRanking++));
            }
        }
    }

    private void saveRankings() {
        System.out.println("Rankings Saved!");
        JSONObject obj = new JSONObject();
        JSONArray jsonRankings = new JSONArray();

        // Add each item from the rankings list to the JSONArray
        for (Object ranking : rankings) {
            jsonRankings.add(ranking);
        }

        obj.put("rankings", jsonRankings);

        try (FileWriter file = new FileWriter("rankings.json")) {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRankings() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("rankings.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonRankings = (JSONArray) jsonObject.get("rankings");

            rankings.clear();
            rankings.addAll(jsonRankings);

            for (Object rankingObj : rankings) {
                JSONObject ranking = (JSONObject) rankingObj;
                String name = (String) ranking.get("name");
                String choice = (String) ranking.get("choice");
                String imagePath = (String) ranking.get("imagePath");

                // Create a label for the rank number
                Label rankNumber = new Label(String.valueOf(rankingCounter++));
                rankNumber.setStyle("-fx-text-fill: yellow; -fx-font-size: 24; -fx-font-family: 'Arial Rounded MT Bold';");

                Label playerName = new Label(name + " | " + choice);
                playerName.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 18));
                playerName.setStyle("-fx-text-fill: " + FOREGROUND_COLOR + "; -fx-background-color: #6a57a9; -fx-border-radius: 15; -fx-padding: 5 15; -fx-background-radius: 15;");

                ImageView playerImageView = null;
                if (imagePath != null && !imagePath.isEmpty()) {
                    playerImageView = new ImageView(new Image(new FileInputStream(imagePath)));
                    playerImageView.setFitHeight(40);
                    playerImageView.setFitWidth(40);
                    playerImageView.setClip(new Circle(20, 20, 20));
                    playerName.setGraphic(playerImageView);
                    playerName.setContentDisplay(ContentDisplay.LEFT);
                }

                HBox playerInfoBox = new HBox(10, playerImageView, playerName);
                playerInfoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                playerInfoBox.setStyle("-fx-background-color: #6a57a9; -fx-border-radius: 15; -fx-background-radius: 15; -fx-padding: 5 15;");

                HBox rankEntry = new HBox(10);
                rankEntry.setSpacing(20);
                rankEntry.setPadding(new Insets(10));
                rankEntry.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                rankEntry.getChildren().addAll(rankNumber, playerInfoBox);

                // Drag Detected Event
                rankEntry.setOnDragDetected(event -> {
                    // Capture a snapshot of the playerInfoBox (excluding the yellow rankings number)
                    SnapshotParameters snapshotParameters = new SnapshotParameters();
                    snapshotParameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
                    WritableImage snapshot = playerInfoBox.snapshot(snapshotParameters, null);

                    // Initiate the drag-and-drop process
                    Dragboard db = rankEntry.startDragAndDrop(TransferMode.MOVE);

                    // Set the drag view to the captured snapshot of the playerInfoBox
                    db.setDragView(snapshot, event.getX(), event.getY());

                    ClipboardContent content = new ClipboardContent();
                    content.putString(String.valueOf(rankingsBox.getChildren().indexOf(rankEntry)));
                    db.setContent(content);
                    event.consume();
                });

                // Drag Over Event
                rankEntry.setOnDragOver(event -> {
                    if (event.getGestureSource() != rankEntry && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                // Drag Dropped Event
                rankEntry.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        int draggedIdx = Integer.parseInt(db.getString());
                        Node draggedNode = rankingsBox.getChildren().get(draggedIdx);
                        rankingsBox.getChildren().remove(draggedIdx);

                        double targetY = event.getY();
                        int targetIndex = 0;
                        boolean isDroppedAtEnd = true;

                        for (Node child : rankingsBox.getChildren()) {
                            if (child.getBoundsInParent().getMinY() > targetY) {
                                isDroppedAtEnd = false;
                                break;
                            }
                            targetIndex++;
                        }

                        if (targetY <= 0) { // Dropped above the list
                            rankingsBox.getChildren().add(0, draggedNode);
                        } else if (isDroppedAtEnd) { // Dropped below the list
                            rankingsBox.getChildren().add(draggedNode);
                        } else {
                            rankingsBox.getChildren().add(targetIndex, draggedNode);
                        }

                        success = true;

                        // Update the yellow ranking numbers
                        updateRankings(rankingsBox);
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });


                rankingsBox.getChildren().add(rankEntry);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Rankings file not found. Starting with default data.");
        } catch (IOException e) {
            System.out.println("Error reading the rankings.json file.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
