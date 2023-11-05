package com.nolte.beerolympics2024;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.text.Font;
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


    public LotteryScreen(List<Object> rankings) {
        this.rankings = rankings;
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
                ballsGroup.getChildren().add(imageHolder);
                ballPanes.add(imageHolder); // Add this line to populate the ballPanes list
            }

            // Create a rotation animation for the group
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(10), ballsGroup);
            rotateTransition.setByAngle(360);
            rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
            rotateTransition.setInterpolator(Interpolator.LINEAR);

            // Start the rotation
            // rotateTransition.play();


            // Apply a counter-rotation to each ball to keep them upright
            for (Node ball : ballsGroup.getChildren()) {
                RotateTransition counterRotate = new RotateTransition(Duration.seconds(10), ball);
                counterRotate.setByAngle(-360);
                counterRotate.setCycleCount(RotateTransition.INDEFINITE);
                counterRotate.setInterpolator(Interpolator.LINEAR);
                counterRotate.play();
            }

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
        // Use recursive calls to move one ball every 3 seconds
        moveNextBall(0);
    }

    private void moveNextBall(int index) {
        if (index >= shuffledRankings.size()) {
            // All balls have been moved
            return;
        }

        JSONObject contestant = shuffledRankings.get(index);
        Object rankObj = contestant.get("rank");
        int rank;

        if (rankObj instanceof Integer) {
            rank = (int) rankObj;
        } else if (rankObj instanceof Long) {
            rank = ((Long) rankObj).intValue();
        } else {
            throw new IllegalStateException("Rank is not a number");
        }

        VBox targetColumn = getTargetColumn(rank);
        StackPane ballPane = ballPanes.get(rank - 1);

        // Ensure the ball is on top when moving
        ballPane.toFront();

        // Calculate the absolute position of the ball
        Point2D ballAbsolutePosition = ballPane.localToScene(ballPane.getLayoutBounds().getCenterX(), ballPane.getLayoutBounds().getCenterY());

        // Calculate the Y-coordinate for the next open row in the target column
        double nextRowY = targetColumn.getLayoutY() + targetColumn.getChildren().size() * (ballPane.getBoundsInParent().getHeight() + 5); // 5 is the spacing between balls

        // Create a dotted line path from the ball's current position to the target column's next open row
        Path path = new Path();
        path.getElements().add(new MoveTo(ballAbsolutePosition.getX(), ballAbsolutePosition.getY()));
        path.getElements().add(new LineTo(targetColumn.getLayoutX() + targetColumn.getWidth() / 2, nextRowY));
        path.setStroke(Color.RED);
        path.setStrokeWidth(2);
        path.getStrokeDashArray().setAll(10.0, 10.0);
        this.getChildren().add(path);

        // Create the path transition
        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.seconds(3));
        transition.setNode(ballPane); // Ensure this is the ball pane that you want to move
        transition.setPath(path);
        transition.setOrientation(PathTransition.OrientationType.NONE); // The ball does not rotate along the path
        transition.setInterpolator(Interpolator.LINEAR); // Move at a constant speed

        // Start the transition
        transition.play();

        // Handle the transition's onFinish event
        transition.setOnFinished(e -> {
            // Remove the dotted line after the transition finishes
            this.getChildren().remove(path);

            // Move the ball to the target column
            targetColumn.getChildren().add(ballPane);
            ballPane.setLayoutX(targetColumn.getLayoutX() + targetColumn.getWidth() / 2 - ballPane.getWidth() / 2); // Center in the VBox
            ballPane.setLayoutY(nextRowY - targetColumn.getLayoutY()); // Adjust Y to be relative to the column

            // Continue with the next ball
            moveNextBall(index + 1);
        });
    }

    // Create the path for the ball to move along
    private Path createPath(double startX, double startY, double endX, double endY) {
        Path path = new Path();
        path.getElements().add(new MoveTo(startX, startY));
        path.getElements().add(new LineTo(endX, endY));
        return path;
    }

    // Determine the target column based on the contestant's rank
    private VBox getTargetColumn(int rank) {
        int totalContestants = rankings.size();
        int threshold = totalContestants / 3;
        if (rank <= threshold) {
            return poolOneColumn;
        } else if (rank <= threshold * 2) {
            return poolTwoColumn;
        } else {
            return poolThreeColumn;
        }
    }
}