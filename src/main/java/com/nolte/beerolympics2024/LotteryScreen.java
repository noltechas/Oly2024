package com.nolte.beerolympics2024;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import javafx.util.Duration;
import org.json.simple.JSONObject;

public class LotteryScreen extends Pane {

    private final double RADIUS = 300; // Radius of the circle on which profile pictures will be placed
    private final double IMAGE_SIZE = 60; // The size of the image (width and height)
    private final double BALL_SIZE = 80; // The size of the white ball background
    private Button startLotteryButton;
    private VBox poolOneColumn, poolTwoColumn, poolThreeColumn;
    private List<StackPane> ballPanes = new ArrayList<>();
    private List<JSONObject> shuffledRankings;
    private List<Object> rankings; // The list of rankings must be a class member
    private Group ballsGroup; // Group for balls so we can manipulate them later
    private List<Contestant> contestants;
    private Map<Node, RotateTransition> counterRotations = new HashMap<>();
    private Group staticBallsGroup = new Group(); // This group will hold balls that are not rotating
    private Map<Node, PathTransition> individualRotations = new HashMap<Node, PathTransition>();


    public LotteryScreen(List<Object> rankings) {
        this.rankings = rankings;
        createContestants();
        // Set the background color of the LotteryScreen
        this.setStyle("-fx-background-color: #3a4ed5;");

        // Use Platform.runLater to wait until the pane has been laid out
        Platform.runLater(() -> {
            // Now we have the width and height
            double centerX = getWidth() / 2;
            double centerY = getHeight() / 2;

            this.ballsGroup = new Group();

            // Position the ballsGroup to the center of the screen
            ballsGroup.setTranslateX(centerX);
            ballsGroup.setTranslateY(centerY);

            // In your initialization or constructor
            this.getChildren().add(staticBallsGroup); // Add the static group to the main container

            staticBallsGroup.layoutXProperty().bind(ballsGroup.layoutXProperty());
            staticBallsGroup.layoutYProperty().bind(ballsGroup.layoutYProperty());

            double angleStep = 360.0 / rankings.size();

            for (int i = 0; i < rankings.size(); i++) {
                JSONObject contestant = (JSONObject) rankings.get(i); // Cast to JSONObject
                String imagePath = (String) contestant.get("imagePath");
                Image image = null;
                try {
                    // Load the image as a square, not preserving the aspect ratio
                    image = new Image(new FileInputStream(imagePath), IMAGE_SIZE, IMAGE_SIZE, false, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ImageView imageView = new ImageView(image);

                // Clip to make the image circular
                Circle clipCircle = new Circle(IMAGE_SIZE / 2);
                imageView.setClip(clipCircle);

                // Center the image view within the clip
                imageView.setX(-IMAGE_SIZE / 2);
                imageView.setY(-IMAGE_SIZE / 2);

                // Create a white circle to represent the ball background
                Circle ballBackground = new Circle(BALL_SIZE / 2);
                ballBackground.setFill(Color.WHITE);

                // StackPane to hold the image and the ball background
                StackPane imageHolder = new StackPane();
                imageHolder.setAlignment(Pos.CENTER);
                imageHolder.getChildren().addAll(ballBackground, imageView); // Add the ball background first so it's behind the image
                imageHolder.setPrefSize(BALL_SIZE, BALL_SIZE);

                // Set the position of the image holder
                double angle = angleStep * i;
                double x = RADIUS * Math.cos(Math.toRadians(angle));
                double y = RADIUS * Math.sin(Math.toRadians(angle));
                imageHolder.setLayoutX(x - BALL_SIZE / 2);
                imageHolder.setLayoutY(y - BALL_SIZE / 2);

                // Add the image holder to the group
                this.getChildren().add(imageHolder);
                ballPanes.add(imageHolder); // Add this line to populate the ballPanes list
            }

            // Define the circular path
            Path circlePath = new Path();
            circlePath.getElements().add(new MoveTo(centerX + RADIUS, centerY)); // Start at the rightmost point of the circle
            circlePath.getElements().add(new ArcTo(RADIUS, RADIUS, 0, centerX - RADIUS, centerY, false, true)); // Arc to the left
            circlePath.getElements().add(new ArcTo(RADIUS, RADIUS, 0, centerX + RADIUS, centerY, false, true)); // Arc back to the start

            // Visualize the path (for debugging purposes)
            circlePath.setStroke(Color.RED);
            circlePath.setStrokeWidth(2);
            circlePath.getStrokeDashArray().setAll(10.0, 10.0);
            circlePath.setFill(null); // Ensure the path is not filled
            this.getChildren().add(circlePath); // Add the path to the screen for visualization

            // Duration for one ball to complete the orbit
            Duration orbitDuration = Duration.seconds(10);

            // Calculate the delay for each ball based on its position in the sequence
            Duration delayBetweenBalls = orbitDuration.divide(rankings.size());

            // Apply the PathTransition to each ballPane
            for (int i = 0; i < rankings.size(); i++) {
                StackPane ballPane = ballPanes.get(i);

                // Create a PathTransition for the ballPane
                PathTransition orbitTransition = new PathTransition();
                orbitTransition.setDuration(orbitDuration);
                orbitTransition.setPath(circlePath);
                orbitTransition.setNode(ballPane);
                // ... [rest of the PathTransition setup]
                orbitTransition.play();
            }

            /*
            // Apply a counter-rotation to each ball to keep them upright
            for (Node ball : ballsGroup.getChildren()) {
                RotateTransition counterRotate = new RotateTransition(Duration.seconds(10), ball);
                counterRotate.setByAngle(-360);
                counterRotate.setCycleCount(RotateTransition.INDEFINITE);
                counterRotate.setInterpolator(Interpolator.LINEAR);
                counterRotate.play();
                counterRotations.put(ball, counterRotate); // Store the transition
            }

             */


            // Initialize the button and columns
            startLotteryButton = createStartButton();
            poolOneColumn = createPoolColumn("Pool One");
            poolTwoColumn = createPoolColumn("Pool Two");
            poolThreeColumn = createPoolColumn("Pool Three");
            double topPadding = 40;  // or any value you prefer for the gap

            poolOneColumn.setLayoutY(topPadding);
            poolTwoColumn.setLayoutY(topPadding);
            poolThreeColumn.setLayoutY(topPadding);

            // Position and hide the columns initially
            // Adjusting the column width to account for padding
            double columnWidth = getWidth() / 3 - 40;  // Subtracting 2 * padding (20 for left and 20 for right)

            poolOneColumn.setLayoutX(20); // 20 pixels padding from the left edge
            poolOneColumn.setPrefHeight(getHeight() - 40);  // Stretch to the screen's height, subtracting 40 for top and bottom padding
            poolOneColumn.setPrefWidth(columnWidth);  // Set width after subtracting padding
            poolOneColumn.setVisible(false);

            poolTwoColumn.setLayoutX(columnWidth + 40); // Account for width and padding of the first column
            poolTwoColumn.setPrefHeight(getHeight() - 40);
            poolTwoColumn.setPrefWidth(columnWidth);
            poolTwoColumn.setVisible(false);

            poolThreeColumn.setLayoutX(2 * columnWidth + 60); // Account for width and padding of the first two columns
            poolThreeColumn.setPrefHeight(getHeight() - 40);
            poolThreeColumn.setPrefWidth(columnWidth);
            poolThreeColumn.setVisible(false);

            poolOneColumn.setPrefHeight(getHeight() - (2 * topPadding));
            poolTwoColumn.setPrefHeight(getHeight() - (2 * topPadding));
            poolThreeColumn.setPrefHeight(getHeight() - (2 * topPadding));

            double padding = 40; // or any value you prefer
            columnWidth = (getWidth() - 4 * padding) / 3;  // Subtracting total padding between and on sides of columns

            poolOneColumn.setLayoutX(padding);
            poolOneColumn.setPrefWidth(columnWidth);

            poolTwoColumn.setLayoutX(2 * padding + columnWidth);
            poolTwoColumn.setPrefWidth(columnWidth);

            poolThreeColumn.setLayoutX(3 * padding + 2 * columnWidth);
            poolThreeColumn.setPrefWidth(columnWidth);

            this.getChildren().addAll(poolOneColumn, poolTwoColumn, poolThreeColumn, ballsGroup);
        });
    }

    private void createContestants() {
        this.contestants = new ArrayList<>();
        for (int i = 0; i < rankings.size(); i++) {
            JSONObject contestant = (JSONObject) rankings.get(i); // Cast to JSONObject
            contestants.add(new Contestant(contestant.get("name").toString(), Integer.parseInt(String.valueOf(contestant.get("rank"))), contestant.get("choice").toString(), contestant.get("imagePath").toString()));
        }
    }

    private VBox createPoolColumn(String name) {
        VBox column = new VBox();
        column.setAlignment(Pos.CENTER);
        column.setSpacing(10); // Provide spacing between items for aesthetics
        column.setPrefWidth(getWidth() / 3);
        column.setPadding(new Insets(20, 20, 20, 20));  // Add 20 pixels padding to the top

        // Apply the blueish-purple rounded rectangle background directly on the VBox
        column.setStyle("-fx-background-color: #6a5acd; -fx-background-radius: 15; -fx-border-radius: 15;");
        column.setAlignment(Pos.TOP_CENTER);

        Label label = new Label(name);
        label.setStyle("-fx-font-size: 24px; -fx-font-family: 'Arial Rounded MT Bold', 'Helvetica', sans-serif;");

        column.getChildren().add(label);

        return column; // Return the VBox with the desired style applied
    }

    private Button createStartButton() {
        Button button = new Button("Start Lottery");
        button.setStyle("-fx-background-color: #FFA500; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 15;");
        button.layoutXProperty().bind(this.widthProperty().subtract(button.widthProperty()).divide(2));
        button.layoutYProperty().bind(this.heightProperty().subtract(button.heightProperty()).divide(2));

        // Set the button's action
        button.setOnAction(event -> {
            button.setVisible(false);
            showColumns();
            // Shuffle the rankings list to determine the order of moving balls
            shuffledRankings = new ArrayList<>();
            for (Object ranking : rankings) {
                shuffledRankings.add((JSONObject) ranking);
            }
            Collections.shuffle(shuffledRankings);
            // Start moving balls one by one
            moveBallsToColumns();
        });

        this.getChildren().add(button);
        return button;
    }

    private void showColumns() {
        poolOneColumn.setVisible(true);
        poolTwoColumn.setVisible(true);
        poolThreeColumn.setVisible(true);
    }

    private void moveBallsToColumns() {
        // Instead of moving balls, draw all paths
        Collections.shuffle(contestants);
        animateBallsToColumns();
    }

    private void animateBallsToColumns() {
        SequentialTransition sequentialTransition = new SequentialTransition();

        for (int i = 0; i < contestants.size(); i++) {
            Contestant contestant = contestants.get(i);
            int sizeOfColumns = contestants.size()/3;
            int rank = contestant.getRank();
            int destinationColumn;
            if (rank <= sizeOfColumns)
                destinationColumn = 0;
            else if (rank <= sizeOfColumns*2)
                destinationColumn = 1;
            else
                destinationColumn = 2;
            int destinationRow = rank - (destinationColumn * sizeOfColumns) - 1;

            StackPane ballPane = ballPanes.get(rank - 1);

            Point2D ballAbsolutePosition = ballPane.localToScene(ballPane.getLayoutBounds().getCenterX(), ballPane.getLayoutBounds().getCenterY());
            Point2D destination = new Point2D(335 + 546*destinationColumn, 185 + destinationRow * 120);

            // Create the path for the ball to follow
            Path path = new Path();
            path.getElements().add(new MoveTo(40, 40));
            LineTo finalDestination = new LineTo(destination.getX()-ballAbsolutePosition.getX(), destination.getY()-ballAbsolutePosition.getY());
            path.getElements().add(finalDestination);

            // Create the path transition
            PathTransition pathTransition = new PathTransition(Duration.seconds(3), path, ballPane);
            pathTransition.setOnFinished(event -> {
                // Stop the individual rotation of the ball
                PathTransition ballRotation = individualRotations.get(ballPane);
                if (ballRotation != null) {
                    ballRotation.stop();
                }

            });

            // Add the transition to the sequence
            sequentialTransition.getChildren().add(pathTransition);
        }

        // Play the animation sequence
        sequentialTransition.play();
    }

}