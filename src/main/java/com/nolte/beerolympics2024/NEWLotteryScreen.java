package com.nolte.beerolympics2024;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;

import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NEWLotteryScreen extends Parent {

    private ArrayList<Contestant> contestants;
    private Pane mainPane;
    private VBox poolOne, poolTwo, poolThree;
    private ArrayList<Circle> balls = new ArrayList<>();
    private Random random = new Random();
    private ArrayList<Group> ballGroups = new ArrayList<>();
    private ArrayList<Group> newBallGroups = new ArrayList<>();
    private boolean created = false;
    private Map<Group, Line> ballLines = new HashMap<>();
    private Map<Group, PathTransition> ballTransitions = new HashMap<>();
    private Rectangle eliteColumn,averageColumn,poorColumn;
    private VBox eliteBox,averageBox,poorBox;
    private Scene scene;
    private Stage primaryStage;

    public NEWLotteryScreen(ArrayList<Contestant> contestants) {
        Collections.shuffle(contestants);
        this.contestants = contestants;
        this.mainPane = new Pane();
        this.getChildren().add(mainPane);
    }

    public void initializeScreen(Stage primaryStage) throws MalformedURLException {
        // Set the stage to be maximized
        this.primaryStage = primaryStage;
        this.primaryStage.setMaximized(true);
        scene = new Scene(this); // No need to set width and height here since we're maximizing

        this.mainPane.prefWidthProperty().bind(scene.widthProperty());
        this.mainPane.prefHeightProperty().bind(scene.heightProperty());

        this.setupLayout();
        this.primaryStage.setScene(scene);
        this.primaryStage.show(); // The window will be maximized on display
        if(!created)
            createRoundedRectanglePathAndBalls();
    }

    private void setupLayout() {
        // Bind the pane's size to the scene's size
        mainPane.setStyle("-fx-background-color: #3a4ed5;");

        // Create the layout with pools and start button
        poolOne = new VBox();
        poolTwo = new VBox();
        poolThree = new VBox();

        mainPane.getChildren().addAll(poolOne, poolTwo, poolThree);

        mainPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            // This will be called after the mainPane has a new width, hence after layout pass
            if (newVal.doubleValue() > 0) { // Check if the new width is greater than 0
                if(!created) {
                    try {
                        createRoundedRectanglePathAndBalls();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        mainPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            // This will be called after the mainPane has a new height, hence after layout pass
            if (newVal.doubleValue() > 0) { // Check if the new height is greater than 0
                if(!created) {
                    try {
                        createRoundedRectanglePathAndBalls();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        double columnWidth = 150;
        double columnHeight = 500;
        Color columnColor = Color.rgb(88, 86, 214); // Purple-blue color
        double cornerRadius = 50;

        // Create the columns
        eliteColumn = new Rectangle(columnWidth, columnHeight);
        eliteColumn.setArcWidth(cornerRadius);
        eliteColumn.setArcHeight(cornerRadius);
        eliteColumn.setFill(columnColor);

        averageColumn = new Rectangle(columnWidth, columnHeight);
        averageColumn.setArcWidth(cornerRadius);
        averageColumn.setArcHeight(cornerRadius);
        averageColumn.setFill(columnColor);

        poorColumn = new Rectangle(columnWidth, columnHeight);
        poorColumn.setArcWidth(cornerRadius);
        poorColumn.setArcHeight(cornerRadius);
        poorColumn.setFill(columnColor);

        // Bind column width and height to mainPane dimensions
        double widthRatio = 0.27; // Adjust this to change the width relative to the pane width
        double heightRatio = 0.75; // Adjust this to change the height relative to the pane height

        eliteColumn.widthProperty().bind(mainPane.widthProperty().multiply(widthRatio));
        eliteColumn.heightProperty().bind(mainPane.heightProperty().multiply(heightRatio));
        averageColumn.widthProperty().bind(mainPane.widthProperty().multiply(widthRatio));
        averageColumn.heightProperty().bind(mainPane.heightProperty().multiply(heightRatio));
        poorColumn.widthProperty().bind(mainPane.widthProperty().multiply(widthRatio));
        poorColumn.heightProperty().bind(mainPane.heightProperty().multiply(heightRatio));

        // Create the labels for the columns
        Text eliteText = new Text("Elite");
        eliteText.setFont(new Font("Arial Rounded MT Bold", 30));
        eliteText.setFill(Color.WHITE);

        Text averageText = new Text("Average");
        averageText.setFont(new Font("Arial Rounded MT Bold", 30));
        averageText.setFill(Color.WHITE);

        Text poorText = new Text("Poor");
        poorText.setFont(new Font("Arial Rounded MT Bold", 30));
        poorText.setFill(Color.WHITE);

        // VBox containers for the columns and labels
        eliteBox = new VBox(5, eliteText, eliteColumn);
        averageBox = new VBox(5, averageText, averageColumn);
        poorBox = new VBox(5, poorText, poorColumn);

        // Align the content to the center of each VBox
        eliteBox.setAlignment(Pos.CENTER);
        averageBox.setAlignment(Pos.CENTER);
        poorBox.setAlignment(Pos.CENTER);

        // HBox to hold the three columns
        HBox columnsBox = new HBox(eliteBox, averageBox, poorBox);
        columnsBox.setAlignment(Pos.CENTER);
        columnsBox.spacingProperty().bind(mainPane.widthProperty().multiply(0.02)); // Adjust the multiplier as needed

        mainPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            double paddingValue = newVal.doubleValue() * 0.25; // 5% of mainPane's width
            Insets paddingInsets = new Insets(0, paddingValue, 0, paddingValue);
            columnsBox.setPadding(paddingInsets);
        });

        // Center the HBox in the main pane
        columnsBox.layoutXProperty().bind(mainPane.widthProperty().subtract(columnsBox.widthProperty()).divide(2));
        columnsBox.layoutYProperty().bind(mainPane.heightProperty().subtract(columnsBox.heightProperty()).divide(2.15));

        columnsBox.setViewOrder(1.0);
        // Add the HBox to the main pane
        mainPane.getChildren().add(columnsBox);

        // Set up the "Start Lottery" button
        setupStartLotteryButton();
    }

    private void createBalls(Path path) throws MalformedURLException {
        // Define the size for the ping pong balls and the images
        double ballDiameter = mainPane.getHeight()/12.5; // Size of the ping pong ball
        double imageDiameter = mainPane.getHeight()/17.5; // Size of the image, slightly smaller than the ball
        Duration totalDuration = Duration.seconds(15);

        // Calculate the path only after the pane has been sized appropriately
        double pathLength = path.getBoundsInLocal().getWidth(); // Rough estimation

        // Calculate the offset increment based on the number of balls
        double offsetIncrement = pathLength / contestants.size();

        ArrayList<Integer> takenSpots = new ArrayList<>();
        for(int i = 0; i < contestants.size(); i++){
            takenSpots.add(contestants.get(i).setRandomRow(contestants.size(),takenSpots));
        }

        // Create a ball for each contestant and space them equally along the path
        for (int i = 0; i < contestants.size(); i++) {
            Contestant contestant = contestants.get(i);

            // Create the white circle for the ping pong ball
            Circle whiteCircle = new Circle(ballDiameter / 2);
            whiteCircle.setFill(Color.WHITE);

            // Create the image with the contestant's picture
            Circle imageCircle = new Circle(imageDiameter / 2);

            File file = new File(contestant.getImagePath());
            String imageURL = file.toURI().toURL().toExternalForm();
            Image image = new Image(imageURL, imageDiameter, imageDiameter, true, true);

            imageCircle.setFill(new ImagePattern(image));

            // Create a group to hold both the white circle and the image
            Group ballGroup = new Group(whiteCircle, imageCircle);

            // Position the imageCircle in the center of the whiteCircle
            imageCircle.setCenterX(whiteCircle.getCenterX());
            imageCircle.setCenterY(whiteCircle.getCenterY());

            // Calculate the ball's final position in its column
            int targetColumn = getTargetColumn(contestant);
            int targetRow = contestant.getRow();
            double finalX = mainPane.getWidth()/4.77 + (mainPane.getWidth()/3.45)*targetColumn;
            double finalY = mainPane.getHeight()/4.5 + (mainPane.getHeight()/8)*targetRow;

            // Create the dotted line
            Line dottedLine = new Line(finalX, finalY, finalX, finalY);
            dottedLine.getStrokeDashArray().addAll(10d, 5d);
            dottedLine.setStroke(Color.RED);
            dottedLine.setViewOrder(-1.0);

            // Adjust the ball's layout offset to match the ball's current position
            double offsetX = finalX;
            double offsetY = finalY;

            // Bind the line's end to the ball's current position
            dottedLine.endXProperty().bind(ballGroup.translateXProperty());
            dottedLine.endYProperty().bind(ballGroup.translateYProperty());

            // Add the dotted line to the mainPane, under the ballGroup
            // mainPane.getChildren().add(dottedLine); // Adding at index 0 to ensure it's under the balls

            // Store the line in a way to access it later
            ballLines.put(ballGroup, dottedLine);

            PathTransition pathTransition = new PathTransition();
            pathTransition.setPath(path);
            pathTransition.setNode(ballGroup);
            pathTransition.setDuration(totalDuration);
            pathTransition.setInterpolator(Interpolator.LINEAR);
            pathTransition.setCycleCount(Timeline.INDEFINITE);
            ballTransitions.put(ballGroup, pathTransition);

            // Jump to the offset that corresponds to the ball's starting position on the path
            double timeOffset = totalDuration.toMillis() * i / contestants.size();
            pathTransition.jumpTo(Duration.millis(timeOffset));
            pathTransition.play();

            ballGroup.setViewOrder(0.0);
            ballGroups.add(ballGroup); // Add the white circle to the list of balls, if needed
            balls.add(imageCircle);
            mainPane.getChildren().add(ballGroup); // Add the group to the mainPane
        }
    }

    private void createRoundedRectanglePathAndBalls() throws MalformedURLException {
        // Now that we know the size of the mainPane, create the path
        double margin = mainPane.getHeight()/17.5; // Ensure the margin is not causing the path to be off-screen
        double cornerRadius; // Ensure corner radius is appropriate
        double width = mainPane.getWidth() - 2 * margin; // Subtract the margins from the total width
        double height = mainPane.getHeight() - 2 * margin; // Subtract the margins from the total height

        if (width > 0 && height > 0) {
            created = true;
            cornerRadius = mainPane.getHeight()/9;

            Path roundedRect = new Path();
            roundedRect.getElements().add(new MoveTo(margin + cornerRadius, margin));
            roundedRect.getElements().add(new HLineTo(margin + width - cornerRadius));
            roundedRect.getElements().add(new ArcTo(cornerRadius, cornerRadius, 0, margin + width, margin + cornerRadius, false, true));
            roundedRect.getElements().add(new VLineTo(margin + height - cornerRadius));
            roundedRect.getElements().add(new ArcTo(cornerRadius, cornerRadius, 0, margin + width - cornerRadius, margin + height, false, true));
            roundedRect.getElements().add(new HLineTo(margin + cornerRadius));
            roundedRect.getElements().add(new ArcTo(cornerRadius, cornerRadius, 0, margin, margin + height - cornerRadius, false, true));
            roundedRect.getElements().add(new VLineTo(margin + cornerRadius));
            roundedRect.getElements().add(new ArcTo(cornerRadius, cornerRadius, 0, margin + cornerRadius, margin, false, true));
            roundedRect.getElements().add(new ClosePath());

            // Call createBalls now that we have our path
            createBalls(roundedRect);
        }
    }

    private void setupStartLotteryButton() {
        Button startLotteryButton = new Button("Start Lottery");
        startLotteryButton.setFont(Font.font("Arial Rounded MT Bold", 20)); // Same font as column text
        startLotteryButton.setStyle("-fx-background-color: #FFA500; -fx-background-radius: 15;"); // Orange-ish color with rounded edges
        startLotteryButton.setTextFill(Color.BLACK);

        // Positioning the button at the bottom of the screen
        StackPane.setAlignment(startLotteryButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startLotteryButton, new Insets(0, 0, 0, 0)); // Adjust the bottom margin as needed

        // Event handler to start moving balls when clicked
        AtomicInteger currentIndex = new AtomicInteger(0);
        startLotteryButton.setOnAction(event -> {
            startLotteryButton.setVisible(false); // Hide the button

            // LOTTERY TIME
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
                int index = currentIndex.getAndIncrement();
                if (index < ballGroups.size()) {
                    Group ballGroup = ballGroups.get(index);
                    moveBallToFinalLocation(ballGroup);
                }
            }));
            timeline.setCycleCount(ballGroups.size());

            // Once all balls have been placed, create the sequence to move them over
            timeline.setOnFinished(e -> {
                PauseTransition pauseBeforeMovingLeft = new PauseTransition(Duration.seconds(3));

                pauseBeforeMovingLeft.setOnFinished(ev -> {
                    ParallelTransition moveBallsTransition = new ParallelTransition();
                    SequentialTransition sequentialTransition = new SequentialTransition();

                    // Transition for moving the balls left
                    for (Group ballGroup : newBallGroups) {
                        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), ballGroup);
                        translateTransition.setByX(-mainPane.getWidth() / 11);
                        moveBallsTransition.getChildren().add(translateTransition);
                    }

                    // Add the ball movement to the sequential transition
                    sequentialTransition.getChildren().add(moveBallsTransition);

                    sequentialTransition.setOnFinished(event1 -> {
                        ArrayList<Text> contestantTexts = new ArrayList<>();
                        // Handle the appearance of the contestant's text
                        for (int i = 0; i < contestants.size(); i++) {
                            Contestant contestant = contestants.get(i);
                            Group ballGroup = newBallGroups.get(i);
                            double finalX = ballGroup.getLayoutX() + ballGroup.getTranslateX() - mainPane.getWidth() / 11;
                            double finalY = ballGroup.getLayoutY() + ballGroup.getTranslateY();

                            Text text = new Text(finalX + mainPane.getWidth()/8, finalY + mainPane.getHeight()/125, contestant.getName() + " - " + contestant.getChoice());
                            contestantTexts.add(text);
                            text.setFont(new Font("Arial Rounded MT Bold", 20));
                            mainPane.getChildren().add(text);
                        }

                        // Delay after text has appeared before starting to fade out the backgrounds
                        PauseTransition delayBeforeFading = new PauseTransition(Duration.seconds(2));

                        delayBeforeFading.setOnFinished(fadeEvent -> {
                            ParallelTransition fadeBackgroundsTransition = new ParallelTransition();

                            FadeTransition fadeBackground = new FadeTransition(Duration.seconds(2), eliteBox);
                            fadeBackground.setToValue(0); // Fading to transparent
                            FadeTransition fadeBackground1 = new FadeTransition(Duration.seconds(2), averageBox);
                            fadeBackground1.setToValue(0); // Fading to transparent
                            FadeTransition fadeBackground2 = new FadeTransition(Duration.seconds(2), poorBox);
                            fadeBackground2.setToValue(0); // Fading to transparent

                            fadeBackgroundsTransition.getChildren().addAll(fadeBackground, fadeBackground1, fadeBackground2);

                            fadeBackgroundsTransition.setOnFinished(rowEvent -> {
                                // Create a parallel transition to handle all row fade-ins simultaneously
                                ParallelTransition fadeInRowsTransition = new ParallelTransition();

                                // Create and add row backgrounds after columns have faded
                                for (int i = 0; i < contestants.size(); i++) {
                                    // Calculate the Y position for the row background based on the ball's final position
                                    Group ballGroup = newBallGroups.get(i);
                                    double yPosition = ballGroup.getLayoutY() + ballGroup.getTranslateY() - ballGroup.getBoundsInParent().getHeight() / 2 - mainPane.getHeight()/75; // 20 pixels higher

                                    // Create the row background rectangle with adjusted height
                                    Rectangle rowBackground = new Rectangle(mainPane.getWidth(), ballGroup.getBoundsInParent().getHeight() + mainPane.getHeight()/35); // 40 pixels taller
                                    rowBackground.setY(yPosition);
                                    rowBackground.setFill(Color.web("#ffbb00"));
                                    rowBackground.setOpacity(0);  // Set initial opacity to 0 for the fade in effect

                                    // Add the row background to the mainPane at the correct position
                                    mainPane.getChildren().add(0, rowBackground);  // Insert at index 0 to ensure it's behind everything else

                                    // Create a fade transition for the row background
                                    FadeTransition fadeInRow = new FadeTransition(Duration.seconds(2), rowBackground);
                                    fadeInRow.setToValue(0.5);  // Target opacity value (half transparent)
                                    fadeInRowsTransition.getChildren().add(fadeInRow);
                                }

                                fadeInRowsTransition.setOnFinished(textFadeEvent -> {
                                    // Fade out the contestant texts after the row backgrounds have faded in
                                    ParallelTransition fadeOutTextsTransition = new ParallelTransition();

                                    for (Text contestantText : contestantTexts) {
                                        FadeTransition fadeTextOut = new FadeTransition(Duration.seconds(1), contestantText);
                                        fadeTextOut.setToValue(0);
                                        fadeOutTextsTransition.getChildren().add(fadeTextOut);
                                    }

                                    fadeOutTextsTransition.setOnFinished(moveBallsEvent -> {
                                        // Now move the balls to predefined X positions for each column
                                        ParallelTransition moveBallsToPositionTransition = new ParallelTransition();

                                        // Predefined X positions for each column (as an example, you can set these according to your layout)
                                        double eliteColumnX = mainPane.getWidth()/20; // Elite balls' X position with 30px buffer
                                        double averageColumnX = eliteColumnX + mainPane.getWidth()/15; // X position for Average column
                                        double poorColumnX = averageColumnX + mainPane.getWidth()/15; // X position for Poor column

                                        for (int i = 0; i < newBallGroups.size(); i++) {
                                            Group ballGroup = newBallGroups.get(i);
                                            Contestant contestant = contestants.get(i);

                                            double targetX;
                                            switch (contestant.getColumn()) { // Assuming 'getColumn()' tells us which column the contestant is in
                                                case 0:
                                                    targetX = eliteColumnX;
                                                    break;
                                                case 1:
                                                    targetX = averageColumnX;
                                                    break;
                                                case 2:
                                                    targetX = poorColumnX;
                                                    break;
                                                default:
                                                    continue; // Skip if we don't recognize the column
                                            }

                                            TranslateTransition moveBallToColumn = new TranslateTransition(Duration.seconds(1), ballGroup);
                                            moveBallToColumn.setToX(targetX - ballGroup.getLayoutX()); // Adjusting target X based on current position and previous translation
                                            moveBallsToPositionTransition.getChildren().add(moveBallToColumn);

                                            moveBallsToPositionTransition.setOnFinished(event4 -> {
                                                double rightmostPosition = mainPane.getWidth()/4.65; // Variable to track the rightmost ball position in each row

                                                for (int row = 0; row < contestants.size()/3; row++) {
                                                    TextField teamNameField = new TextField("Team Name");
                                                    teamNameField.setLayoutX(rightmostPosition);
                                                    teamNameField.setLayoutY(mainPane.getHeight()/5.5 + (mainPane.getHeight()/7.95*row));
                                                    teamNameField.setFont(new Font("Arial Rounded MT Bold", mainPane.getHeight()/25));
                                                    teamNameField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

                                                    TextField anthemField = new TextField("National Anthem");
                                                    anthemField.setLayoutX(rightmostPosition + mainPane.getWidth()/5);
                                                    anthemField.setLayoutY(mainPane.getHeight()/5.5 + (mainPane.getHeight()/7.95*row));
                                                    anthemField.setFont(new Font("Arial Rounded MT Bold", mainPane.getHeight()/25));
                                                    anthemField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

                                                    // Add the text field to the pane
                                                    mainPane.getChildren().add(teamNameField);
                                                    mainPane.getChildren().add(anthemField);
                                                }

                                                // Pause for 20 seconds before showing the Continue button
                                                PauseTransition pauseBeforeContinueButton = new PauseTransition(Duration.seconds(2));
                                                pauseBeforeContinueButton.setOnFinished(event5 -> {
                                                    Button continueButton = new Button("Continue");
                                                    continueButton.setLayoutX(mainPane.getWidth()*0.87); // Set to bottom right corner
                                                    continueButton.setLayoutY(mainPane.getHeight()*0.9);
                                                    continueButton.setFont(new Font("Arial Rounded MT Bold", mainPane.getHeight()/25));
                                                    // Apply a similar style as your other buttons, you might have a specific style class or inline style
                                                    continueButton.setStyle("-fx-background-color: #ffbb00; -fx-text-fill: black; -fx-background-radius: 15;");

                                                    // Add the Continue button to the pane
                                                    mainPane.getChildren().add(continueButton);

                                                    // List to hold all created Team objects
                                                    List<Team> teams = new ArrayList<>();

                                                    continueButton.setOnAction(event6 -> {
                                                        int startingNegative = 2 + (contestants.size()/3)*2 + contestants.size();
                                                        for(int k = 0; k < contestants.size(); k++)
                                                            getContestantFromBallGroup((Group) mainPane.getChildren().get(mainPane.getChildren().size()-(startingNegative+k)));

                                                        ArrayList<TextField> textFields = new ArrayList<>();
                                                        for (int j = 0; j < mainPane.getChildren().size(); j++) {
                                                            if (mainPane.getChildren().get(j) instanceof TextField) {
                                                                textFields.add((TextField) mainPane.getChildren().get(j));
                                                            }
                                                        }
                                                        textFields.sort(new Comparator<TextField>() {
                                                            @Override
                                                            public int compare(TextField t1, TextField t2) {
                                                                return Double.compare(t1.getLayoutY(), t2.getLayoutY());
                                                            }
                                                        });
                                                        contestants.sort(new Comparator<Contestant>() {
                                                            @Override
                                                            public int compare(Contestant t1, Contestant t2) {
                                                                return Double.compare(t1.getyValue(), t2.getyValue());
                                                            }
                                                        });

                                                        for(int j = 0; j < contestants.size()/3; j++){
                                                            ArrayList<Contestant> contestantsOnTeam = new ArrayList<>();
                                                            contestantsOnTeam.add(contestants.get(j*3));
                                                            contestantsOnTeam.add(contestants.get(j*3 + 1));
                                                            contestantsOnTeam.add(contestants.get(j*3 + 2));
                                                            teams.add(new Team(contestantsOnTeam, textFields.get(j*2).getText(),textFields.get((j*2)+1).getText()));
                                                        }

                                                        for(int j = 0; j < teams.size(); j++){
                                                            teams.get(j).printInfo();
                                                        }

                                                        // Navigate to the new team screen with the teams list
                                                        LeaderboardScreen leaderboardScreen = new LeaderboardScreen(teams, primaryStage);
                                                        leaderboardScreen.display();
                                                        primaryStage.close();
                                                    });
                                                });

                                                // Start the pause transition
                                                pauseBeforeContinueButton.play();

                                            });
                                        }

                                        // Start the transition to move balls to the respective column positions
                                        moveBallsToPositionTransition.play();
                                    });
                                    // Start the fade out texts transition after row backgrounds have faded in
                                    fadeOutTextsTransition.play();
                                });

                                // Start fading in rows after the columns have faded out
                                fadeInRowsTransition.play();
                            });

                            // Start fading backgrounds after the delay
                            fadeBackgroundsTransition.play();
                        });

                        // Start the pause transition after texts are placed
                        delayBeforeFading.play();
                    });

                    // Start the sequence of transitions
                    sequentialTransition.play();
                });

                // Start the initial pause before moving balls
                pauseBeforeMovingLeft.play();
            });

            timeline.play();
        });

        startLotteryButton.layoutXProperty().bind(mainPane.widthProperty().subtract(startLotteryButton.widthProperty()).divide(2));
        startLotteryButton.layoutYProperty().bind(mainPane.heightProperty().subtract(startLotteryButton.heightProperty()).subtract(25)); // 20 is the margin from the bottom

        // Add the button to the main pane
        mainPane.getChildren().add(startLotteryButton);
    }

    private Contestant getContestantFromBallGroup(Group ballGroup) {
        Circle picture = (Circle) ballGroup.getChildren().get(1);
        ImagePattern imagePattern = (ImagePattern) picture.getFill();
        String URL = imagePattern.getImage().getUrl();

        // Convert local coordinates to screen coordinates
        Bounds boundsInScreen = picture.localToScreen(picture.getBoundsInLocal());

        for (Contestant contestant : this.contestants) {
            if (Objects.equals(contestant.getImagePath(), URL)) {
                contestant.setyValue(boundsInScreen.getMinY());
                return contestant;
            }
        }
        return null;
    }


    private int getTargetColumn(Contestant contestant) {
        int rank = contestant.getRank();
        int thirdSize = contestants.size() / 3;
        if (rank <= thirdSize) {
            return 0;
        } else if (rank <= thirdSize * 2) {
            return 1;
        } else {
            return 2;
        }
    }

    private void moveBallToFinalLocation(Group ballGroup) {
        // Stop the PathTransition for this ball
        PathTransition pathTransition = ballTransitions.get(ballGroup);
        if (pathTransition != null) {
            pathTransition.stop();
        }

        // Remove the ballGroup from the mainPane
        mainPane.getChildren().remove(ballGroup);

        // Create a new identical ball at the same location as the old ball
        Group newBallGroup = new Group();
        for (Node child : ballGroup.getChildren()) {
            if (child instanceof Circle) {
                Circle circle = new Circle(((Circle) child).getCenterX(), ((Circle) child).getCenterY(), ((Circle) child).getRadius());
                circle.setFill(((Circle) child).getFill());
                newBallGroup.getChildren().add(circle);
            }
        }
        newBallGroup.setLayoutX(ballGroup.getLayoutX() + ballGroup.getTranslateX());
        newBallGroup.setLayoutY(ballGroup.getLayoutY() + ballGroup.getTranslateY());

        // Get the final location for this ball
        Line dottedLine = ballLines.get(ballGroup);
        double finalX = dottedLine.getStartX();
        double finalY = dottedLine.getStartY();

        // Add the new ballGroup to the mainPane
        mainPane.getChildren().add(newBallGroup);

        // Animate the new ballGroup to the final location
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), newBallGroup);
        translateTransition.setToX(finalX - newBallGroup.getLayoutX());
        translateTransition.setToY(finalY - newBallGroup.getLayoutY());
        translateTransition.setOnFinished(event -> {
            // Remove the dotted line after the ball reaches the final location
            mainPane.getChildren().remove(dottedLine);
        });
        newBallGroups.add(newBallGroup);
        translateTransition.play();
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}