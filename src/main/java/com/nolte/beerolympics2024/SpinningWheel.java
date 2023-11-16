package com.nolte.beerolympics2024;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinningWheel extends Pane {
    private List<GameSlice> gameSlices;
    private Label selectedGameLabel;
    private Random random;
    private Line spinner;
    private double wheelRadius;

    public SpinningWheel(double radius) {
        this.wheelRadius = radius;
        gameSlices = new ArrayList<>();
        random = new Random();

        selectedGameLabel = new Label("Spin to select a game");
        selectedGameLabel.setLayoutX(radius - 100); // Position the label
        selectedGameLabel.setLayoutY(radius - 20);

        spinner = new Line(radius, radius - wheelRadius, radius, radius);
        spinner.setStrokeWidth(2);
        spinner.setStroke(Color.BLACK);

        this.getChildren().addAll(selectedGameLabel, spinner);
    }

    public void addGame(String gameName, Color color) {
        GameSlice slice = new GameSlice(gameName, color, wheelRadius);
        gameSlices.add(slice);
        updateWheel();
    }

    public void removeGame(String gameName) {
        gameSlices.removeIf(slice -> slice.gameName.equals(gameName));
        updateWheel();
    }

    public void spin() {
        if (gameSlices.isEmpty()) {
            selectedGameLabel.setText("No games to spin");
            return;
        }

        int randomIndex = random.nextInt(gameSlices.size());
        GameSlice selectedSlice = gameSlices.get(randomIndex);
        double angleToSpin = 360 * 5 + selectedSlice.midAngle; // Ensure a few full rotations

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(3), this);
        rotateTransition.setByAngle(angleToSpin);
        rotateTransition.setOnFinished(event -> selectedGameLabel.setText("Selected game: " + selectedSlice.gameName));
        rotateTransition.play();
    }

    private void updateWheel() {
        // Clear previous slices and text, but keep the spinner and label
        this.getChildren().removeIf(node -> node instanceof Arc || node instanceof Text);

        double totalAngle = 0;
        double sliceAngle = 360.0 / gameSlices.size(); // Calculate the angle for each slice

        for (GameSlice slice : gameSlices) {
            slice.setStartAngle(totalAngle, sliceAngle); // Pass the calculated slice angle
            this.getChildren().add(slice.arc); // Add the arc first
            totalAngle += sliceAngle;
        }

        // Now add the text nodes, so they are on top
        for (GameSlice slice : gameSlices) {
            this.getChildren().add(slice.text); // Add the text after all arcs have been added
        }
    }

    private class GameSlice {
        String gameName;
        Arc arc;
        Text text;
        double angle;
        double midAngle;

        GameSlice(String gameName, Color color, double radius) {
            this.gameName = gameName;
            this.arc = new Arc(wheelRadius, wheelRadius, radius, radius, 0, angle); // The angle will be set later
            arc.setFill(color);
            arc.setType(ArcType.ROUND);

            this.text = new Text(gameName);
            text.setFont(new Font(16));
            // The positionText will be called after angle is set
        }

        void setStartAngle(double startAngle, double sliceAngle) {
            this.angle = sliceAngle; // The slice angle should be passed from the updateWheel method
            this.arc.setStartAngle(startAngle);
            this.arc.setLength(angle); // Set the length of the arc to match the angle
            this.midAngle = startAngle + angle / 2.0;
            positionText();
        }

        private void positionText() {
            // Calculate the midpoint angle of the slice in radians
            double midAngleRadians = Math.toRadians(midAngle);

            // The radius where the text should be placed
            double textRadius = wheelRadius * 0.7;

            // Calculate the text position
            double textX = wheelRadius + textRadius * Math.cos(midAngleRadians - Math.PI / 2);
            double textY = wheelRadius + textRadius * Math.sin(midAngleRadians - Math.PI / 2);

            // Set the text's position
            text.setLayoutX(textX);
            text.setLayoutY(textY);

            // Center the text around its position
            text.setTranslateX(-text.getBoundsInLocal().getWidth() / 2);
            text.setTranslateY(text.getBoundsInLocal().getHeight() / 4);

            // Clear any existing transforms
            text.getTransforms().clear();

            // Create a rotation transform to rotate the text around its center
            Rotate rotate = new Rotate(-midAngle, textX, textY);

            // Add the rotation to the text
            text.getTransforms().add(rotate);
        }

    }
}