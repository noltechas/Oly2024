package com.nolte.beerolympics2024;

import javafx.animation.RotateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.List;

public class LotteryScreen extends Pane {

    private final double RADIUS = 100; // Radius of the circle on which profile pictures will be placed

    public LotteryScreen(List<Object> rankings) {
        Circle whiteCircle = new Circle(RADIUS + 20); // Slightly larger white circle
        whiteCircle.setFill(Color.WHITE);
        whiteCircle.setCenterX(getWidth() / 2);
        whiteCircle.setCenterY(getHeight() / 2);
        getChildren().add(whiteCircle);

        double angleStep = 360.0 / rankings.size();

        for (int i = 0; i < rankings.size(); i++) {
            Image image = rankings.get(i).getImage(); // Assuming RankEntry has a method getImage() that returns the contestant's image
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40); // Adjust size as needed
            imageView.setFitHeight(40);
            imageView.setPreserveRatio(true);

            double angle = angleStep * i;
            double x = getWidth() / 2 + RADIUS * Math.cos(Math.toRadians(angle));
            double y = getHeight() / 2 + RADIUS * Math.sin(Math.toRadians(angle));

            imageView.setX(x - imageView.getFitWidth() / 2);
            imageView.setY(y - imageView.getFitHeight() / 2);

            getChildren().add(imageView);
        }

        RotateTransition rotate = new RotateTransition(Duration.seconds(10), this);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.play();
    }
}
