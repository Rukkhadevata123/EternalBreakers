package com.eternalbreaker.view;

import com.eternalbreaker.model.GameConstants;
import com.eternalbreaker.model.GameVersion;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class GameView {
    private final BorderPane mainPane;
    private final Pane gamePane;
    private final Circle gameBall;
    private final Rectangle playerPaddle;
    private final Rectangle[][] gameBricks;
    private final Label scoreDisplayLabel;
    private final Label roundDisplayLabel;
    private final Label statusMessageLabel;
    private final Label ballSpeedLabel;
    private final Label paddleLengthLabel;
    private final Label totalTimeUsedLabel;
    private final Label lastRoundTimeLabel;
    private final Label versionLabel;

    private final Button slowSpeedButton;
    private final Button normalSpeedButton;
    private final Button fastSpeedButton;
    private final Button longLengthButton;
    private final Button normalLengthButton;
    private final Button shortLengthButton;
    private final Button hellMButton;
    private final Button randomModeButton;
    private final Button startButton;

    public GameView() {
        // Create the main pane
        mainPane = new BorderPane();

        // Create the menu pane and add it to the top of the main pane
        GridPane menuPane = new GridPane();
        menuPane.setAlignment(Pos.CENTER);
        menuPane.setHgap(10);
        menuPane.setVgap(10);
        mainPane.setTop(menuPane);

        // Create the labels and buttons for the menu pane
        Label speedLabel = new Label("Ball Speed:");
        slowSpeedButton = new Button("Slow(" + GameConstants.BALL_SPEED_SLOW + ")");
        normalSpeedButton = new Button("Normal(" + GameConstants.BALL_SPEED_NORMAL + ")");
        fastSpeedButton = new Button("Fast(" + GameConstants.BALL_SPEED_FAST + ")");
        Label lengthLabel = new Label("Paddle Length:");
        longLengthButton = new Button("Long(" + GameConstants.PADDLE_LENGTH_LONG + ")");
        normalLengthButton = new Button("Normal(" + GameConstants.PADDLE_LENGTH_NORMAL + ")");
        shortLengthButton = new Button("Short(" + GameConstants.PADDLE_LENGTH_SHORT + ")");
        Label modeLabel = new Label("Mode:");
        hellMButton = new Button("Hell Mode");
        randomModeButton = new Button("Random Mode");
        startButton = new Button("Start");

        slowSpeedButton.setPrefWidth(100);
        normalSpeedButton.setPrefWidth(100);
        fastSpeedButton.setPrefWidth(100);
        longLengthButton.setPrefWidth(100);
        normalLengthButton.setPrefWidth(100);
        shortLengthButton.setPrefWidth(100);
        startButton.setPrefWidth(100);
        hellMButton.setPrefWidth(100);
        randomModeButton.setPrefWidth(100);

        // Add the labels and buttons to the menu pane
        menuPane.add(speedLabel, 0, 0);
        menuPane.add(slowSpeedButton, 1, 0);
        menuPane.add(normalSpeedButton, 2, 0);
        menuPane.add(fastSpeedButton, 3, 0);
        menuPane.add(lengthLabel, 0, 1);
        menuPane.add(longLengthButton, 1, 1);
        menuPane.add(normalLengthButton, 2, 1);
        menuPane.add(shortLengthButton, 3, 1);
        menuPane.add(modeLabel, 0, 2);
        menuPane.add(hellMButton, 1, 2);
        menuPane.add(startButton, 2, 2);
        menuPane.add(randomModeButton, 3, 2);

        // Create the game pane and add it to the center of the main pane
        gamePane = new Pane();
        mainPane.setCenter(gamePane);

        // Create the gameBall and add it to the game pane
        gameBall = new Circle((double) GameConstants.SCREEN_WIDTH / 2, (double) GameConstants.SCREEN_HEIGHT / 2, GameConstants.BALL_SIZE_RADIUS);
        gameBall.setFill(Color.BLACK);
        gamePane.getChildren().add(gameBall);

        // Create the playerPaddle and add it to the game pane
        playerPaddle = new Rectangle((double) GameConstants.SCREEN_WIDTH / 2 - GameConstants.PADDLE_LENGTH_NORMAL / 2, GameConstants.PADDLE_START_Y, GameConstants.PADDLE_LENGTH_NORMAL, GameConstants.PADDLE_HEIGHT_CONSTANT);
        playerPaddle.setFill(Color.BLACK);
        gamePane.getChildren().add(playerPaddle);

        // Create the gameBricks and add them to the game pane
        gameBricks = new Rectangle[GameConstants.TOTAL_BRICK_ROWS][GameConstants.TOTAL_BRICK_COLUMNS];
        for (int i = 0; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
            for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                gameBricks[i][j] = new Rectangle(j * GameConstants.BRICK_TILE_WIDTH + GameConstants.BRICK_TILE_GAP, i * GameConstants.BRICK_TILE_HEIGHT + GameConstants.BRICK_TILE_GAP, GameConstants.BRICK_TILE_WIDTH - 2 * GameConstants.BRICK_TILE_GAP, GameConstants.BRICK_TILE_HEIGHT - 2 * GameConstants.BRICK_TILE_GAP);
                if (i == 0) {
                    gameBricks[i][j].setFill(Color.RED);
                } else {
                    gameBricks[i][j].setFill(Color.hsb(i * 360.0 / GameConstants.TOTAL_BRICK_ROWS, 0.8, 0.8));
                }
                gamePane.getChildren().add(gameBricks[i][j]);
            }
        }

        // Create the labels for the game status
        scoreDisplayLabel = createLabel("Score: 0", 10, 100, true);
        roundDisplayLabel = createLabel("Round: 1", 10, 130, true);
        lastRoundTimeLabel = createLabel("Time used in the last round: 0.0 seconds", 10, 170, true);
        totalTimeUsedLabel = createLabel("Time used in all rounds: 0.0 seconds", 10, 200, true);
        ballSpeedLabel = createLabel("Speed: Normal(" + GameConstants.BALL_SPEED_NORMAL + ")", 10, 240, true);
        paddleLengthLabel = createLabel("Length: Normal(" + GameConstants.PADDLE_LENGTH_NORMAL + ")", 10, 270, true);
        statusMessageLabel = createLabel("Get ready!", (double) (GameConstants.SCREEN_WIDTH - 100) / 2, (double) GameConstants.SCREEN_HEIGHT / 2 - 100, false);

        // Add version label
        versionLabel = createLabel("Version: " + GameVersion.VERSION, 10, 310, true);
    }

    private Label createLabel(String text, double x, double y, boolean visible) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setStyle(
            "-fx-background-color: rgba(72, 69, 69, 0.7);" +
            "-fx-padding: 5px 10px;" +
            "-fx-background-radius: 5px;" +
            "-fx-effect: dropshadow(gaussian, rgba(58, 50, 50, 0.5), 5, 0, 0, 1);" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;"
        );
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setVisible(visible);
        gamePane.getChildren().add(label);
        return label;
    }

    public BorderPane getMainPane() {
        return mainPane;
    }

    public Circle getGameBall() {
        return gameBall;
    }

    public Rectangle getPlayerPaddle() {
        return playerPaddle;
    }

    public Rectangle[][] getGameBricks() {
        return gameBricks;
    }

    public Label getScoreDisplayLabel() {
        return scoreDisplayLabel;
    }

    public Label getRoundDisplayLabel() {
        return roundDisplayLabel;
    }

    public Label getStatusMessageLabel() {
        return statusMessageLabel;
    }

    public Label getBallSpeedLabel() {
        return ballSpeedLabel;
    }

    public Label getPaddleLengthLabel() {
        return paddleLengthLabel;
    }

    public Label getTotalTimeUsedLabel() {
        return totalTimeUsedLabel;
    }

    public Label getLastRoundTimeLabel() {
        return lastRoundTimeLabel;
    }

    public Label getVersionLabel() {
        return versionLabel;
    }

    public Button getSlowSpeedButton() {
        return slowSpeedButton;
    }

    public Button getNormalSpeedButton() {
        return normalSpeedButton;
    }

    public Button getFastSpeedButton() {
        return fastSpeedButton;
    }

    public Button getLongLengthButton() {
        return longLengthButton;
    }

    public Button getNormalLengthButton() {
        return normalLengthButton;
    }

    public Button getShortLengthButton() {
        return shortLengthButton;
    }

    public Button getHellMButton() {
        return hellMButton;
    }

    public Button getRandomModeButton() {
        return randomModeButton;
    }

    public Button getStartButton() {
        return startButton;
    }
}