package com.eternalbreaker.controller;

import com.eternalbreaker.model.GameConstants;
import com.eternalbreaker.model.GameState;
import com.eternalbreaker.model.Stopwatch;
import com.eternalbreaker.view.GameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.concurrent.ThreadLocalRandom;

public class GameController {
    private final GameView gameView;
    private final GameState gameState;
    private final Scene scene;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private Timeline animation;
    private Timeline statusMessageTimeline;
    private Timeline gameStartCountdownTimeline;
    private Timeline resetCountdownTimeline;

    private boolean gameIsRunning = false;
    private boolean resetInProgress = false;
    private boolean randomModeActive = false;

    private double ballVelocityX = GameConstants.BALL_SPEED_NORMAL;
    private double ballVelocityY = GameConstants.BALL_SPEED_NORMAL;
    private double ballTrajectoryAngle = Math.PI / 4;

    public GameController(GameView gameView, GameState gameState, Scene scene) {
        this.gameView = gameView;
        this.gameState = gameState;
        this.scene = scene;

        initialize();
    }

    private void initialize() {
        scene.setOnMouseMoved(this::handleMouseMoved);

        animation = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            try {
                moveBall();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);

        gameView.getSlowSpeedButton().setOnAction(e -> setBallSpeed(GameConstants.BALL_SPEED_SLOW));
        gameView.getNormalSpeedButton().setOnAction(e -> setBallSpeed(GameConstants.BALL_SPEED_NORMAL));
        gameView.getFastSpeedButton().setOnAction(e -> setBallSpeed(GameConstants.BALL_SPEED_FAST));
        gameView.getLongLengthButton().setOnAction(e -> setPaddleLength(GameConstants.PADDLE_LENGTH_LONG));
        gameView.getNormalLengthButton().setOnAction(e -> setPaddleLength(GameConstants.PADDLE_LENGTH_NORMAL));
        gameView.getShortLengthButton().setOnAction(e -> setPaddleLength(GameConstants.PADDLE_LENGTH_SHORT));
        gameView.getHellMButton().setOnAction(e -> setHellMode());
        gameView.getRandomModeButton().setOnAction(e -> setRandomMode());
        gameView.getStartButton().setOnAction(e -> startGame());
    }

    private void setBallSpeed(double speed) {
        if (!randomModeActive && ballVelocityX != GameConstants.BALL_SPEED_HELL || !gameIsRunning) {
            ballVelocityX = ballVelocityY = speed;
            randomModeActive = false;
            gameView.getBallSpeedLabel().setText("Speed: " + speed);
        }
    }

    private void setPaddleLength(double length) {
        if (!randomModeActive && ballVelocityX != GameConstants.BALL_SPEED_HELL || !gameIsRunning) {
            gameView.getPlayerPaddle().setWidth(length);
            gameView.getPlayerPaddle().setVisible(true);
            randomModeActive = false;
            gameView.getPaddleLengthLabel().setText("Length: " + length);
        }
    }

    private void setHellMode() {
        if (!gameIsRunning) {
            ballVelocityX = ballVelocityY = GameConstants.BALL_SPEED_HELL;
            randomModeActive = false;
            gameView.getPlayerPaddle().setWidth(GameConstants.PADDLE_LENGTH_NORMAL);
            gameView.getPlayerPaddle().setVisible(false);

            gameView.getBallSpeedLabel().setText("Speed: HELL(" + GameConstants.BALL_SPEED_HELL + ")");
            gameView.getPaddleLengthLabel().setText("Length: TRANSPARENT(" + GameConstants.PADDLE_LENGTH_NORMAL + ")");
        }
    }

    private void setRandomMode() {
        if (!gameIsRunning) {
            randomModeActive = true;
            changeSpeed();
            changeLength();
        }
    }

    private void startGame() {
        if (!gameIsRunning) {
            gameIsRunning = true;
            resetInProgress = true;
            resetGame();
            showAndHideMessage("Get ready!", 1);
            gameState.setPreparationTimeLeft(3);
            startCountdown();
        }
    }

    private void startCountdown() {
        gameStartCountdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (gameState.getPreparationTimeLeft() > 0) {
                String message = "Game starting in " + gameState.getPreparationTimeLeft() + " seconds...";
                showAndHideMessage(message, 1);
                gameState.setPreparationTimeLeft(gameState.getPreparationTimeLeft() - 1);
            } else {
                showAndHideMessage("Game started!", 3);
                animation.play();
                gameState.setGameRoundStopwatch(new Stopwatch());
                gameState.setBrickResetStopwatch(new Stopwatch());
                updateScoreLabel();
                updateRoundLabel();
                gameState.setPreparationTimeLeft(0);
                gameStartCountdownTimeline.stop();
                gameIsRunning = true;
            }
        }));
        gameStartCountdownTimeline.setCycleCount(4);
        gameStartCountdownTimeline.play();
    }

    private void changeSpeed() {
        Platform.runLater(() -> {
            int randomSpeed = random.nextInt(3);
            switch (randomSpeed) {
                case 0:
                    ballVelocityX = ballVelocityY = randomModeActive ? GameConstants.BALL_SPEED_SLOW * (1 + Math.random()) : GameConstants.BALL_SPEED_SLOW;
                    gameView.getBallSpeedLabel().setText("Speed: Slow(" + GameConstants.BALL_SPEED_SLOW + ")");
                    break;
                case 1:
                    ballVelocityX = ballVelocityY = randomModeActive ? GameConstants.BALL_SPEED_NORMAL * (1 + Math.random()) : GameConstants.BALL_SPEED_NORMAL;
                    gameView.getBallSpeedLabel().setText("Speed: Normal(" + GameConstants.BALL_SPEED_NORMAL + ")");
                    break;
                case 2:
                    ballVelocityX = ballVelocityY = randomModeActive ? GameConstants.BALL_SPEED_FAST * (1 + Math.random()) : GameConstants.BALL_SPEED_FAST;
                    gameView.getBallSpeedLabel().setText("Speed: Fast(" + GameConstants.BALL_SPEED_FAST + ")");
                    break;
            }
        });
    }

    private void changeLength() {
        Platform.runLater(() -> {
            int randomLength = random.nextInt(3);
            switch (randomLength) {
                case 0:
                    gameView.getPlayerPaddle().setWidth(randomModeActive ? GameConstants.PADDLE_LENGTH_SHORT * (1 + Math.random()) : GameConstants.PADDLE_LENGTH_SHORT);
                    gameView.getPlayerPaddle().setVisible(true);
                    gameView.getPaddleLengthLabel().setText("Length: Short(" + GameConstants.PADDLE_LENGTH_SHORT + ")");
                    break;
                case 1:
                    gameView.getPlayerPaddle().setWidth(randomModeActive ? GameConstants.PADDLE_LENGTH_NORMAL * (1 + Math.random()) : GameConstants.PADDLE_LENGTH_NORMAL);
                    gameView.getPlayerPaddle().setVisible(true);
                    gameView.getPaddleLengthLabel().setText("Length: Normal(" + GameConstants.PADDLE_LENGTH_NORMAL + ")");
                    break;
                case 2:
                    gameView.getPlayerPaddle().setWidth(randomModeActive ? GameConstants.PADDLE_LENGTH_LONG * (1 + Math.random()) : GameConstants.PADDLE_LENGTH_LONG);
                    gameView.getPlayerPaddle().setVisible(true);
                    gameView.getPaddleLengthLabel().setText("Length: Long(" + GameConstants.PADDLE_LENGTH_LONG + ")");
                    break;
            }
        });
    }

    private void handleMouseMoved(MouseEvent e) {
        double mouseX = e.getX();
        gameView.getPlayerPaddle().setX(mouseX - gameView.getPlayerPaddle().getWidth() / 2);
    }

    private void showAndHideMessage(String message, int duration) {
        if (statusMessageTimeline != null) {
            statusMessageTimeline.stop();
        }
        gameView.getStatusMessageLabel().setText(message);
        gameView.getStatusMessageLabel().setVisible(true);
        statusMessageTimeline = new Timeline(new KeyFrame(Duration.seconds(duration), e -> gameView.getStatusMessageLabel().setVisible(false)));
        statusMessageTimeline.play();
    }

    private void updateScoreLabel() {
        Timeline scoreUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> gameView.getScoreDisplayLabel().setText("Score: " + gameState.getPlayerScore())));
        scoreUpdater.setCycleCount(Timeline.INDEFINITE);
        scoreUpdater.play();
    }

    private void updateRoundLabel() {
        Timeline roundUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> gameView.getRoundDisplayLabel().setText("Round: " + gameState.getCurrentGameRound())));
        roundUpdater.setCycleCount(Timeline.INDEFINITE);
        roundUpdater.play();
    }

    private void updateTotalTimeUsedLabel() {
        Timeline totalTimeUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> gameView.getTotalTimeUsedLabel().setText("Total time used: " + gameState.getTotalElapsedTime() + " seconds")));
        totalTimeUpdater.setCycleCount(Timeline.INDEFINITE);
        totalTimeUpdater.play();
    }

    private void updateLastRoundTimeUsedLabel() {
        Timeline lastRoundTimeUpdater = new Timeline(new KeyFrame(Duration.millis(100), e -> gameView.getLastRoundTimeLabel().setText("Last round time used: " + gameState.getLastRoundElapsedTime() + " seconds")));
        lastRoundTimeUpdater.setCycleCount(Timeline.INDEFINITE);
        lastRoundTimeUpdater.play();
    }

    private void updateScore() {
        if (randomModeActive) {
            gameState.setPlayerScore(gameState.getPlayerScore() + (int) (Math.random() * 10)); // Add a random number from 0 to 9 to the playerScore
        }
        if (ballVelocityX == GameConstants.BALL_SPEED_HELL && ballVelocityY == GameConstants.BALL_SPEED_HELL) {
            gameState.setPlayerScore(gameState.getPlayerScore() + 6);
        }
        if (!gameView.getPlayerPaddle().isVisible()) {
            gameState.setPlayerScore(gameState.getPlayerScore() + 6);
        }
        if (ballVelocityX != GameConstants.BALL_SPEED_HELL && ballVelocityY != GameConstants.BALL_SPEED_HELL) {
            switch ((int) ballVelocityX) {
                case (int) GameConstants.BALL_SPEED_SLOW:
                    gameState.setPlayerScore(gameState.getPlayerScore() + 1);
                    break;
                case (int) GameConstants.BALL_SPEED_NORMAL:
                    gameState.setPlayerScore(gameState.getPlayerScore() + 2);
                    break;
                case (int) GameConstants.BALL_SPEED_FAST:
                    gameState.setPlayerScore(gameState.getPlayerScore() + 3);
                    break;
            }
            switch ((int) gameView.getPlayerPaddle().getWidth()) {
                case (int) GameConstants.PADDLE_LENGTH_LONG:
                    gameState.setPlayerScore(gameState.getPlayerScore() + 1);
                    break;
                case (int) GameConstants.PADDLE_LENGTH_NORMAL:
                    gameState.setPlayerScore(gameState.getPlayerScore() + 2);
                    break;
                case (int) GameConstants.PADDLE_LENGTH_SHORT:
                    gameState.setPlayerScore(gameState.getPlayerScore() + 3);
                    break;
            }
        }
    }

    private void resetGame() {
        gameView.getGameBall().setCenterX((double) GameConstants.SCREEN_WIDTH / 2);
        gameView.getGameBall().setCenterY((double) GameConstants.SCREEN_HEIGHT / 2);
        gameView.getPlayerPaddle().setX((double) GameConstants.SCREEN_WIDTH / 2 - GameConstants.PADDLE_LENGTH_NORMAL / 2);

        for (int i = 0; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
            for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                gameView.getGameBricks()[i][j].setVisible(true);
            }
        }

        gameState.setPlayerScore(0);
        gameState.setCurrentGameRound(1);
        updateScoreLabel();
        updateRoundLabel();

        gameState.setTotalElapsedTime(0);
        gameState.setLastRoundElapsedTime(0);
        updateTotalTimeUsedLabel();
        updateLastRoundTimeUsedLabel();

        gameState.setGameRoundStopwatch(new Stopwatch());
        gameState.setBrickResetStopwatch(new Stopwatch());
    }

    private void restoreBricks() {
        for (int i = 0; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
            for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                gameView.getGameBricks()[i][j].setVisible(true);
            }
        }
        gameState.setCurrentGameRound(gameState.getCurrentGameRound() + 1);
        resetInProgress = true;
        // gameState.setResetCountdownTime(10);
        gameState.setBrickResetStopwatch(new Stopwatch());
    }

    private double calculateHitFactor(double ballCenterX, double paddleX, double paddleWidth, boolean randomModeActive) {
        double hitFactor = (ballCenterX - paddleX) / paddleWidth - 0.5;
        if (randomModeActive) {
            hitFactor += (Math.random() - 0.5) / 2.0;
        }
        return hitFactor;
    }

    private void handleCollision(double ballCenterX, double paddleX, double paddleWidth, boolean randomModeActive) {
        double hitFactor = calculateHitFactor(ballCenterX, paddleX, paddleWidth, randomModeActive);
        if (hitFactor == 0) {
            ballTrajectoryAngle = Math.PI / 2;
        } else {
            ballTrajectoryAngle = hitFactor < 0 ? Math.PI - Math.max(hitFactor * -Math.PI / 2, Math.PI / 6) : Math.max(hitFactor * Math.PI / 2, Math.PI / 6);
        }
    }

    private void moveBall() throws InterruptedException {
        // Check if the gameBall hits the left or right wall
        if (gameView.getGameBall().getCenterX() < GameConstants.BALL_SIZE_RADIUS + 1 || gameView.getGameBall().getCenterX() > GameConstants.SCREEN_WIDTH - GameConstants.BALL_SIZE_RADIUS - 1) {
            ballTrajectoryAngle = Math.PI - ballTrajectoryAngle;
            gameView.getGameBall().setCenterX(gameView.getGameBall().getCenterX() < GameConstants.BALL_SIZE_RADIUS + 1 ? GameConstants.BALL_SIZE_RADIUS + 1 : GameConstants.SCREEN_WIDTH - GameConstants.BALL_SIZE_RADIUS - 1);

            if (randomModeActive) {
                ballTrajectoryAngle += random.nextDouble() * 0.3 - 0.15;
            }
        }

        // Check if the gameBall hits the top wall
        if (gameView.getGameBall().getCenterY() < GameConstants.BALL_SIZE_RADIUS + GameConstants.BRICK_TILE_HEIGHT) {
            ballTrajectoryAngle = -ballTrajectoryAngle;
            gameView.getGameBall().setCenterY(GameConstants.BALL_SIZE_RADIUS + GameConstants.BRICK_TILE_HEIGHT + 1);

            if (randomModeActive) {
                ballTrajectoryAngle += random.nextDouble() * 0.3 - 0.15;
            }
        }

        // Check if the gameBall hits the bottom wall
        if (gameView.getGameBall().getCenterY() > GameConstants.SCREEN_HEIGHT - GameConstants.BALL_SIZE_RADIUS) {
            animation.stop();
            showAndHideMessage("Game over! Your score is " + gameState.getPlayerScore() + ".", 3);
            gameIsRunning = false;
            randomModeActive = false;

            String speedText = gameView.getBallSpeedLabel().getText();
            if (speedText.contains("(") && speedText.contains(")")) {
                String speedValueText = speedText.substring(speedText.indexOf("(") + 1, speedText.indexOf(")"));
                double speedValue = Double.parseDouble(speedValueText);
                ballVelocityX = ballVelocityY = speedValue;
            }

            String lengthText = gameView.getPaddleLengthLabel().getText();
            if (lengthText.contains("(") && lengthText.contains(")")) {
                String lengthValueText = lengthText.substring(lengthText.indexOf("(") + 1, lengthText.indexOf(")"));
                double lengthValue = Double.parseDouble(lengthValueText);
                gameView.getPlayerPaddle().setWidth(lengthValue);
            }

            gameView.getPlayerPaddle().setVisible(ballVelocityX != GameConstants.BALL_SPEED_HELL || !lengthText.equals("Length: TRANSPARENT(" + GameConstants.PADDLE_LENGTH_NORMAL + ")"));

            if (resetCountdownTimeline != null) {
                resetCountdownTimeline.stop();
            }
        }

        // Check if the gameBall hits the playerPaddle
        if (gameView.getGameBall().intersects(gameView.getPlayerPaddle().getBoundsInLocal())) {
            handleCollision(gameView.getGameBall().getCenterX(), gameView.getPlayerPaddle().getX(), gameView.getPlayerPaddle().getWidth(), randomModeActive);
            gameView.getGameBall().setCenterY(gameView.getPlayerPaddle().getY() - gameView.getGameBall().getRadius());
            updateScore();
        }

        // Check if the gameBall hits any brick
        outerLoop:
        for (int i = 0; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
            for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                if (gameView.getGameBricks()[i][j].isVisible() && gameView.getGameBall().intersects(gameView.getGameBricks()[i][j].getBoundsInLocal()) && !Color.RED.equals(gameView.getGameBricks()[i][j].getFill())) {
                    handleCollision(gameView.getGameBall().getCenterX(), gameView.getPlayerPaddle().getX(), gameView.getPlayerPaddle().getWidth(), randomModeActive);
                    gameView.getGameBricks()[i][j].setVisible(false);
                    updateScore();
                    break outerLoop;
                }
            }
        }

        if (randomModeActive && random.nextInt(100) < 5) {
            changeSpeed();
            changeLength();
        }

        double randomFactor = randomModeActive ? (0.7 + Math.random() * 0.3) : 1.0;
        gameView.getGameBall().setCenterX(gameView.getGameBall().getCenterX() + ballVelocityX * Math.cos(ballTrajectoryAngle) * randomFactor);
        gameView.getGameBall().setCenterY(gameView.getGameBall().getCenterY() - ballVelocityY * Math.sin(ballTrajectoryAngle) * randomFactor);

        if (resetInProgress) {
            boolean allGameBricksBroken = true;
            for (int i = 1; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
                for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                    if (gameView.getGameBricks()[i][j].isVisible()) {
                        allGameBricksBroken = false;
                        break;
                    }
                }
                if (!allGameBricksBroken) {
                    break;
                }
            }

            if (allGameBricksBroken) {
                resetInProgress = false;
                gameState.setResetCountdownTime(10);
                showAndHideMessage("All bricks broken! Resetting in 10 seconds...", 1);
                gameState.getGameRoundStopwatch().pause();
                gameState.setTotalElapsedTime(gameState.getGameRoundStopwatch().elapsedTime());
                gameState.setLastRoundElapsedTime(gameState.getBrickResetStopwatch().elapsedTime());
                updateTotalTimeUsedLabel();
                updateLastRoundTimeUsedLabel();

                resetCountdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                    if (gameState.getResetCountdownTime() > 0) {
                        String message = "Resetting in " + gameState.getResetCountdownTime() + " seconds...";
                        showAndHideMessage(message, 1);
                        gameState.setResetCountdownTime(gameState.getResetCountdownTime() - 1);
                    } else {
                        showAndHideMessage("Game restarted!", 3);
                        gameState.setResetCountdownTime(0);
                        restoreBricks();
                        resetCountdownTimeline.stop();
                        gameState.getGameRoundStopwatch().resume();
                    }
                }));
                resetCountdownTimeline.setCycleCount(11);
                resetCountdownTimeline.play();
            }
        }
    }
}