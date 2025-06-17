package com.eternalbreaker;

import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

  // Game Constants - Exactly copied from original
  private static final int SCREEN_WIDTH = 600;
  private static final int SCREEN_HEIGHT = 400;
  private static final int BALL_SIZE_RADIUS = 10;
  private static final int PADDLE_HEIGHT_CONSTANT = 10;
  private static final int PADDLE_START_Y = SCREEN_HEIGHT - 50;
  private static final double PADDLE_LENGTH_LONG = 160;
  private static final double PADDLE_LENGTH_NORMAL = 80;
  private static final double PADDLE_LENGTH_SHORT = 40;
  private static final int TOTAL_BRICK_ROWS = 5;
  private static final int TOTAL_BRICK_COLUMNS = 10;
  private static final int BRICK_TILE_WIDTH = SCREEN_WIDTH / TOTAL_BRICK_COLUMNS;
  private static final int BRICK_TILE_HEIGHT = 15;
  private static final int BRICK_TILE_GAP = 4;
  private static final double BALL_SPEED_SLOW = 8;
  private static final double BALL_SPEED_NORMAL = 9;
  private static final double BALL_SPEED_FAST = 10;
  private static final double BALL_SPEED_HELL = 15;

  // Game Objects
  private Circle ball;
  private Rectangle paddle;
  private Rectangle[][] bricks;
  private Label scoreLabel;
  private Label speedLabel;
  private Label paddleLabel;
  private Label statusLabel;

  // Game State
  private int score = 0;
  private double ballVelocityX = BALL_SPEED_NORMAL;
  private double ballVelocityY = BALL_SPEED_NORMAL;
  private double ballAngle = Math.PI / 4;
  private boolean gameRunning = false;
  private boolean hellMode = false;
  private boolean randomMode = false;

  // Animation
  private Timeline gameLoop;
  private Timeline countdown;
  private Timeline messageHider; // 专门用于隐藏消息的Timeline

  @Override
  public void start(Stage primaryStage) {
    BorderPane root = createGameLayout();
    Scene scene = new Scene(root, SCREEN_WIDTH + 200, SCREEN_HEIGHT + 140);

    // Mouse control for paddle
    scene.setOnMouseMoved(this::handleMouseMove);

    primaryStage.setTitle("Eternal Breaker - Brick Breaking Game");
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();

    // Shutdown hook
    primaryStage.setOnCloseRequest(e -> shutdown());
  }

  private BorderPane createGameLayout() {
    BorderPane root = new BorderPane();

    // Create game panel
    Pane gamePane = new Pane();
    gamePane.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
    gamePane.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2;");

    // Create game objects
    createGameObjects(gamePane);

    // Create UI panels
    root.setTop(createTopControlPanel());
    root.setLeft(createLeftInfoPanel());
    root.setCenter(gamePane);

    return root;
  }

  private void createGameObjects(Pane gamePane) {
    // Create ball
    ball = new Circle(SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, BALL_SIZE_RADIUS);
    ball.setFill(Color.BLACK);
    ball.setStroke(Color.DARKGRAY);

    // Create paddle
    paddle =
        new Rectangle(
            SCREEN_WIDTH / 2.0 - PADDLE_LENGTH_NORMAL / 2,
            PADDLE_START_Y,
            PADDLE_LENGTH_NORMAL,
            PADDLE_HEIGHT_CONSTANT);
    paddle.setFill(Color.BLACK);
    paddle.setStroke(Color.DARKGRAY);

    // Create bricks with rainbow colors
    bricks = new Rectangle[TOTAL_BRICK_ROWS][TOTAL_BRICK_COLUMNS];
    Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE};

    for (int i = 0; i < TOTAL_BRICK_ROWS; i++) {
      for (int j = 0; j < TOTAL_BRICK_COLUMNS; j++) {
        bricks[i][j] =
            new Rectangle(
                j * BRICK_TILE_WIDTH + BRICK_TILE_GAP,
                i * BRICK_TILE_HEIGHT + BRICK_TILE_GAP + 30,
                BRICK_TILE_WIDTH - 2 * BRICK_TILE_GAP,
                BRICK_TILE_HEIGHT - 2 * BRICK_TILE_GAP);
        bricks[i][j].setFill(colors[i]);
        bricks[i][j].setStroke(Color.BLACK);
        bricks[i][j].setStrokeWidth(0.5);
        gamePane.getChildren().add(bricks[i][j]);
      }
    }

    // Create status message
    statusLabel = new Label("Ready");
    statusLabel.setStyle(
        "-fx-background-color: rgba(255,255,255,0.95); "
            + "-fx-border-color: black; -fx-border-width: 2; "
            + "-fx-padding: 15; -fx-font-weight: bold; -fx-font-size: 14; "
            + "-fx-text-alignment: center;");
    statusLabel.setLayoutX(SCREEN_WIDTH / 2 - 75);
    statusLabel.setLayoutY(SCREEN_HEIGHT / 2 - 25);
    statusLabel.setVisible(false);

    gamePane.getChildren().addAll(ball, paddle, statusLabel);
  }

  private VBox createTopControlPanel() {
    VBox topPanel = new VBox(8);
    topPanel.setPadding(new Insets(15));
    topPanel.setStyle(
        "-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");

    // Speed controls
    HBox speedBox = new HBox(8);
    speedBox.setAlignment(Pos.CENTER);
    Label speedLabelTitle = new Label("Ball Speed:");
    speedLabelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    speedLabelTitle.setPrefWidth(80);

    Button slowBtn = createButton("Slow", e -> setBallSpeed(BALL_SPEED_SLOW));
    Button normalBtn = createButton("Normal", e -> setBallSpeed(BALL_SPEED_NORMAL));
    Button fastBtn = createButton("Fast", e -> setBallSpeed(BALL_SPEED_FAST));

    speedBox.getChildren().addAll(speedLabelTitle, slowBtn, normalBtn, fastBtn);

    // Paddle controls
    HBox paddleBox = new HBox(8);
    paddleBox.setAlignment(Pos.CENTER);
    Label paddleLabelTitle = new Label("Paddle Size:");
    paddleLabelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    paddleLabelTitle.setPrefWidth(80);

    Button longBtn = createButton("Long Paddle", e -> setPaddleLength(PADDLE_LENGTH_LONG));
    Button normalPaddleBtn =
        createButton("Normal Paddle", e -> setPaddleLength(PADDLE_LENGTH_NORMAL));
    Button shortBtn = createButton("Short Paddle", e -> setPaddleLength(PADDLE_LENGTH_SHORT));

    paddleBox.getChildren().addAll(paddleLabelTitle, longBtn, normalPaddleBtn, shortBtn);

    // Game mode controls
    HBox modeBox = new HBox(8);
    modeBox.setAlignment(Pos.CENTER);
    Label modeLabelTitle = new Label("Game Mode:");
    modeLabelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    modeLabelTitle.setPrefWidth(80);

    Button hellBtn = createButton("Hell Mode", e -> setHellMode());
    Button randomBtn = createButton("Random Mode", e -> toggleRandomMode());
    Button startBtn = createButton("Start Game", e -> startGame());

    modeBox.getChildren().addAll(modeLabelTitle, hellBtn, randomBtn, startBtn);

    topPanel.getChildren().addAll(speedBox, paddleBox, modeBox);
    return topPanel;
  }

  private VBox createLeftInfoPanel() {
    VBox leftPanel = new VBox(8);
    leftPanel.setPadding(new Insets(15));
    leftPanel.setPrefWidth(200);
    leftPanel.setStyle(
        "-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 0 1 0 0;");

    Label infoTitle = new Label("Game Info");
    infoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    infoTitle.setStyle("-fx-text-fill: #333; -fx-padding: 0 0 10 0;");

    scoreLabel = createInfoLabel("Score: 0");
    speedLabel = createInfoLabel("Speed: " + (int) BALL_SPEED_NORMAL);
    paddleLabel = createInfoLabel("Paddle: " + (int) PADDLE_LENGTH_NORMAL);

    leftPanel.getChildren().addAll(infoTitle, scoreLabel, speedLabel, paddleLabel);
    return leftPanel;
  }

  private Button createButton(String text, EventHandler<ActionEvent> action) {
    Button btn = new Button(text);
    btn.setPrefSize(110, 32);
    btn.setStyle(
        "-fx-background-color: #f8f9fa; "
            + "-fx-border-color: #6c757d; "
            + "-fx-border-width: 1; "
            + "-fx-border-radius: 4; "
            + "-fx-background-radius: 4; "
            + "-fx-font-size: 11; "
            + "-fx-font-weight: bold; "
            + "-fx-cursor: hand;");

    btn.setOnAction(
        e -> {
          if (!gameRunning) {
            action.handle(e);
          }
        });

    return btn;
  }

  private Label createInfoLabel(String text) {
    Label label = new Label(text);
    label.setStyle(
        "-fx-background-color: #f8f9fa; "
            + "-fx-border-color: #dee2e6; "
            + "-fx-border-width: 1; "
            + "-fx-border-radius: 3; "
            + "-fx-background-radius: 3; "
            + "-fx-padding: 8; "
            + "-fx-font-size: 12; "
            + "-fx-font-family: 'Courier New', monospace;");
    label.setPrefWidth(170);
    return label;
  }

  private void setBallSpeed(double speed) {
    ballVelocityX = speed;
    ballVelocityY = speed;
    hellMode = (speed == BALL_SPEED_HELL);
    randomMode = false;

    if (hellMode) {
      paddle.setVisible(false);
      speedLabel.setText("Speed: " + (int) speed);
      paddleLabel.setText("Paddle: INVISIBLE");
    } else {
      paddle.setVisible(true);
      speedLabel.setText("Speed: " + (int) speed);
    }
  }

  private void setPaddleLength(double length) {
    if (!hellMode) {
      paddle.setWidth(length);
      paddle.setVisible(true);
      randomMode = false;
      paddleLabel.setText("Paddle: " + (int) length);
    }
  }

  private void setHellMode() {
    hellMode = true;
    randomMode = false;
    ballVelocityX = BALL_SPEED_HELL;
    ballVelocityY = BALL_SPEED_HELL;
    paddle.setWidth(PADDLE_LENGTH_NORMAL);
    paddle.setVisible(false);
    speedLabel.setText("Speed: " + (int) BALL_SPEED_HELL);
    paddleLabel.setText("Paddle: INVISIBLE");
  }

  private void toggleRandomMode() {
    randomMode = !randomMode;
    hellMode = false;

    if (randomMode) {
      showMessage("Random mode ON", 2);
      changeRandomSettings();
    } else {
      showMessage("Random mode OFF", 2);
      setBallSpeed(BALL_SPEED_NORMAL);
      setPaddleLength(PADDLE_LENGTH_NORMAL);
    }
  }

  private void changeRandomSettings() {
    if (!randomMode) return;

    // Random speed
    double[] speeds = {BALL_SPEED_SLOW, BALL_SPEED_NORMAL, BALL_SPEED_FAST};
    double randomSpeed = speeds[ThreadLocalRandom.current().nextInt(3)] * (1 + Math.random());
    ballVelocityX = randomSpeed;
    ballVelocityY = randomSpeed;
    speedLabel.setText("Speed: " + String.format("%.0f", randomSpeed));

    // Random paddle length
    double[] lengths = {PADDLE_LENGTH_SHORT, PADDLE_LENGTH_NORMAL, PADDLE_LENGTH_LONG};
    double randomLength = lengths[ThreadLocalRandom.current().nextInt(3)] * (1 + Math.random());
    paddle.setWidth(randomLength);
    paddle.setVisible(true);
    paddleLabel.setText("Paddle: " + String.format("%.0f", randomLength));
  }

  private void startGame() {
    if (gameRunning) return;

    // Reset game
    resetGame();

    // Start countdown with fixed timing
    int[] countdownTime = {3};

    countdown =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                e -> {
                  if (countdownTime[0] > 1) {
                    showMessage("Starting in " + countdownTime[0] + "...", 0); // 不自动隐藏
                    countdownTime[0]--;
                  } else if (countdownTime[0] == 1) {
                    showMessage("Starting in 1...", 0); // 不自动隐藏
                    countdownTime[0]--;
                  } else {
                    showMessage("GO!", 1); // 显示1秒后自动隐藏
                    countdown.stop();
                    startGameLoop();
                  }
                }));
    countdown.setCycleCount(4); // 3秒倒计时 + GO消息
    countdown.play();
  }

  private void startGameLoop() {
    gameRunning = true;

    gameLoop =
        new Timeline(
            new KeyFrame(
                Duration.millis(10),
                e -> {
                  moveBall();
                  checkCollisions();
                  updateScore();

                  // Random mode effects
                  if (randomMode && Math.random() < 0.05) {
                    changeRandomSettings();
                  }
                }));
    gameLoop.setCycleCount(Timeline.INDEFINITE);
    gameLoop.play();
  }

  private void moveBall() {
    double randomFactor = randomMode ? (0.7 + Math.random() * 0.3) : 1.0;

    ball.setCenterX(ball.getCenterX() + ballVelocityX * Math.cos(ballAngle) * randomFactor);
    ball.setCenterY(ball.getCenterY() - ballVelocityY * Math.sin(ballAngle) * randomFactor);
  }

  private void checkCollisions() {
    // Wall collisions
    checkWallCollisions();

    // Paddle collision
    checkPaddleCollision();

    // Brick collisions
    checkBrickCollisions();

    // Bottom wall - game over
    if (ball.getCenterY() > SCREEN_HEIGHT - BALL_SIZE_RADIUS) {
      gameOver();
    }

    // Check victory
    if (areAllBricksDestroyed()) {
      victory();
    }
  }

  private void checkWallCollisions() {
    // Left and right walls
    if (ball.getCenterX() < BALL_SIZE_RADIUS + 1
        || ball.getCenterX() > SCREEN_WIDTH - BALL_SIZE_RADIUS - 1) {
      ballAngle = Math.PI - ballAngle;
      ball.setCenterX(
          ball.getCenterX() < BALL_SIZE_RADIUS + 1
              ? BALL_SIZE_RADIUS + 1
              : SCREEN_WIDTH - BALL_SIZE_RADIUS - 1);

      if (randomMode) {
        ballAngle += (Math.random() * 0.2 - 0.1);
      }
    }

    // Top wall (modified - no longer red brick collision)
    if (ball.getCenterY() < BALL_SIZE_RADIUS + 1) {
      ballAngle = -ballAngle;
      ball.setCenterY(BALL_SIZE_RADIUS + 1);

      if (randomMode) {
        ballAngle += (Math.random() * 0.2 - 0.1);
      }
    }
  }

  private void checkPaddleCollision() {
    if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
      double hitFactor = calculateHitFactor(ball.getCenterX(), paddle.getX(), paddle.getWidth());

      if (hitFactor == 0) {
        ballAngle = Math.PI / 2;
      } else {
        ballAngle =
            hitFactor < 0
                ? Math.PI - Math.max(hitFactor * -Math.PI / 2, Math.PI / 6)
                : Math.max(hitFactor * Math.PI / 2, Math.PI / 6);
      }

      if (randomMode) {
        ballAngle += (Math.random() * 0.2 - 0.1);
      }

      ball.setCenterY(paddle.getY() - ball.getRadius());
      score += getScoreIncrement();
    }
  }

  private void checkBrickCollisions() {
    for (int i = 0; i < TOTAL_BRICK_ROWS; i++) {
      for (int j = 0; j < TOTAL_BRICK_COLUMNS; j++) {
        Rectangle brick = bricks[i][j];
        if (brick.isVisible() && ball.getBoundsInParent().intersects(brick.getBoundsInParent())) {
          handleBrickCollision(brick);
          brick.setVisible(false);
          score += getScoreIncrement();
          return; // Only handle one collision per frame
        }
      }
    }
  }

  private void handleBrickCollision(Rectangle brick) {
    double ballCenterX = ball.getCenterX();
    double ballCenterY = ball.getCenterY();
    double brickX = brick.getX();
    double brickY = brick.getY();
    double brickWidth = brick.getWidth();
    double brickHeight = brick.getHeight();

    // Calculate overlaps
    double overlapLeft = ballCenterX + BALL_SIZE_RADIUS - brickX;
    double overlapRight = brickX + brickWidth - (ballCenterX - BALL_SIZE_RADIUS);
    double overlapTop = ballCenterY + BALL_SIZE_RADIUS - brickY;
    double overlapBottom = brickY + brickHeight - (ballCenterY - BALL_SIZE_RADIUS);

    double minOverlap =
        Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

    if (minOverlap == overlapLeft || minOverlap == overlapRight) {
      // Horizontal collision
      ballAngle = Math.PI - ballAngle;
    } else {
      // Vertical collision
      ballAngle = -ballAngle;
    }

    if (randomMode) {
      ballAngle += (Math.random() * 0.2 - 0.1);
    }

    // Prevent ball from getting stuck
    if (minOverlap == overlapLeft) {
      ball.setCenterX(brickX - BALL_SIZE_RADIUS - 1);
    } else if (minOverlap == overlapRight) {
      ball.setCenterX(brickX + brickWidth + BALL_SIZE_RADIUS + 1);
    } else if (minOverlap == overlapTop) {
      ball.setCenterY(brickY - BALL_SIZE_RADIUS - 1);
    } else if (minOverlap == overlapBottom) {
      ball.setCenterY(brickY + brickHeight + BALL_SIZE_RADIUS + 1);
    }
  }

  private double calculateHitFactor(double ballCenterX, double paddleX, double paddleWidth) {
    double hitFactor = (ballCenterX - paddleX) / paddleWidth - 0.5;
    if (randomMode) {
      hitFactor += (Math.random() - 0.5) / 2.0;
    }
    return hitFactor;
  }

  private int getScoreIncrement() {
    int baseScore = 1;

    if (hellMode || !paddle.isVisible()) {
      baseScore += 6;
    }

    if (randomMode) {
      baseScore += (int) (Math.random() * 10);
    }

    // Speed bonus
    if (ballVelocityX == BALL_SPEED_SLOW) baseScore += 1;
    else if (ballVelocityX == BALL_SPEED_NORMAL) baseScore += 2;
    else if (ballVelocityX == BALL_SPEED_FAST) baseScore += 3;

    // Paddle size bonus
    double paddleWidth = paddle.getWidth();
    if (paddleWidth == PADDLE_LENGTH_LONG) baseScore += 1;
    else if (paddleWidth == PADDLE_LENGTH_NORMAL) baseScore += 2;
    else if (paddleWidth == PADDLE_LENGTH_SHORT) baseScore += 3;

    return baseScore;
  }

  private boolean areAllBricksDestroyed() {
    for (int i = 0; i < TOTAL_BRICK_ROWS; i++) {
      for (int j = 0; j < TOTAL_BRICK_COLUMNS; j++) {
        if (bricks[i][j].isVisible()) {
          return false;
        }
      }
    }
    return true;
  }

  private void updateScore() {
    scoreLabel.setText("Score: " + score);
  }

  private void gameOver() {
    gameRunning = false;
    if (gameLoop != null) gameLoop.stop();

    String message = String.format("Game Over!\nScore: %d\n\nClick Start to restart", score);
    showMessage(message, 999);

    resetModes();
  }

  private void victory() {
    gameRunning = false;
    if (gameLoop != null) gameLoop.stop();

    String message =
        String.format(
            "Victory!\nAll Bricks Destroyed!\nScore: %d\n\nClick Start to play again", score);
    showMessage(message, 999);

    resetModes();
  }

  private void resetGame() {
    // Reset ball position
    ball.setCenterX(SCREEN_WIDTH / 2.0);
    ball.setCenterY(SCREEN_HEIGHT / 2.0);
    ballAngle = Math.PI / 4;

    // Reset paddle position
    paddle.setX(SCREEN_WIDTH / 2.0 - paddle.getWidth() / 2);

    // Reset bricks
    for (int i = 0; i < TOTAL_BRICK_ROWS; i++) {
      for (int j = 0; j < TOTAL_BRICK_COLUMNS; j++) {
        bricks[i][j].setVisible(true);
      }
    }

    // Reset score
    score = 0;
    updateScore();
  }

  private void resetModes() {
    randomMode = false;
    hellMode = false;
    ballVelocityX = BALL_SPEED_NORMAL;
    ballVelocityY = BALL_SPEED_NORMAL;
    paddle.setWidth(PADDLE_LENGTH_NORMAL);
    paddle.setVisible(true);
    speedLabel.setText("Speed: " + (int) BALL_SPEED_NORMAL);
    paddleLabel.setText("Paddle: " + (int) PADDLE_LENGTH_NORMAL);
  }

  private void showMessage(String message, int duration) {
    // 停止之前的消息隐藏器
    if (messageHider != null) {
      messageHider.stop();
    }

    statusLabel.setText(message);
    statusLabel.setVisible(true);

    // 只有duration > 0 且 duration < 999时才自动隐藏
    if (duration > 0 && duration < 999) {
      messageHider =
          new Timeline(
              new KeyFrame(Duration.seconds(duration), e -> statusLabel.setVisible(false)));
      messageHider.play();
    }
    // duration = 0 时不自动隐藏，duration >= 999 时也不自动隐藏（需要手动处理）
  }

  private void handleMouseMove(MouseEvent e) {
    double mouseX = e.getX();
    double paddleWidth = paddle.getWidth();
    double newX = Math.max(0, Math.min(SCREEN_WIDTH - paddleWidth, mouseX - paddleWidth / 2));
    paddle.setX(newX);
  }

  private void shutdown() {
    if (gameLoop != null) gameLoop.stop();
    if (countdown != null) countdown.stop();
    if (messageHider != null) messageHider.stop();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
