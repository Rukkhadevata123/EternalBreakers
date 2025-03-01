package com.eternalbreaker.controller;

import com.eternalbreaker.model.GameConstants;
import com.eternalbreaker.view.GameView;
import javafx.scene.paint.Color;

public class BallPhysicsManager {
    private final GameView gameView;
    private final ScoreManager scoreManager;
    private GameModeManager gameModeManager;

    private double ballVelocityX = GameConstants.BALL_SPEED_NORMAL;
    private double ballVelocityY = GameConstants.BALL_SPEED_NORMAL;
    private double ballTrajectoryAngle = Math.PI / 4;

    public BallPhysicsManager(GameView gameView, ScoreManager scoreManager, GameModeManager gameModeManager) {
        this.gameView = gameView;
        this.scoreManager = scoreManager;
        this.gameModeManager = gameModeManager;
    }

    public void setBallVelocity(double velocity) {
        this.ballVelocityX = velocity;
        this.ballVelocityY = velocity;
    }

    public double getBallVelocityX() {
        return ballVelocityX;
    }

    public double getBallVelocityY() {
        return ballVelocityY;
    }

    public void moveBall() {
        handleWallCollisions();
        handlePaddleCollision();
        handleBrickCollisions();

        // 新增: 随机模式下随机改变速度和长度
        if (gameModeManager.isRandomModeActive() && Math.random() < 0.05) {
            gameModeManager.changeSpeed();
            gameModeManager.changeLength();
        }

        double randomFactor = gameModeManager.isRandomModeActive() ? (0.7 + Math.random() * 0.3) : 1.0;

        gameView.getGameBall().setCenterX(gameView.getGameBall().getCenterX() +
                ballVelocityX * Math.cos(ballTrajectoryAngle) * randomFactor);
        gameView.getGameBall().setCenterY(gameView.getGameBall().getCenterY() -
                ballVelocityY * Math.sin(ballTrajectoryAngle) * randomFactor);
    }

    public boolean checkBottomWallCollision() {
        return gameView.getGameBall().getCenterY() > GameConstants.SCREEN_HEIGHT - GameConstants.BALL_SIZE_RADIUS;
    }

    // 确保BallPhysicsManager中的检测方法正确
    public boolean checkAllBricksBroken() {
        // 只检查非第一行的砖块，因为第一行是特殊红色砖块
        for (int i = 1; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
            for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                if (gameView.getGameBricks()[i][j].isVisible()) {
                    return false;
                }
            }
        }
        System.out.println("所有砖块已经被打破！");
        return true;
    }

    private void handleWallCollisions() {
        // 检查左右墙壁碰撞
        if (gameView.getGameBall().getCenterX() < GameConstants.BALL_SIZE_RADIUS + 1 ||
                gameView.getGameBall().getCenterX() > GameConstants.SCREEN_WIDTH - GameConstants.BALL_SIZE_RADIUS - 1) {

            ballTrajectoryAngle = Math.PI - ballTrajectoryAngle;
            gameView.getGameBall()
                    .setCenterX(gameView.getGameBall().getCenterX() < GameConstants.BALL_SIZE_RADIUS + 1
                            ? GameConstants.BALL_SIZE_RADIUS + 1
                            : GameConstants.SCREEN_WIDTH - GameConstants.BALL_SIZE_RADIUS - 1);

            if (gameModeManager.isRandomModeActive()) {
                ballTrajectoryAngle += (Math.random() * 0.3 - 0.15);
            }
        }

        // 检查上墙壁碰撞
        if (gameView.getGameBall().getCenterY() < GameConstants.BALL_SIZE_RADIUS + GameConstants.BRICK_TILE_HEIGHT) {
            ballTrajectoryAngle = -ballTrajectoryAngle;
            gameView.getGameBall().setCenterY(GameConstants.BALL_SIZE_RADIUS + GameConstants.BRICK_TILE_HEIGHT + 1);

            if (gameModeManager.isRandomModeActive()) {
                ballTrajectoryAngle += (Math.random() * 0.3 - 0.15);
            }
        }
    }

    private void handlePaddleCollision() {
        if (gameView.getGameBall().intersects(gameView.getPlayerPaddle().getBoundsInLocal())) {
            double hitFactor = calculateHitFactor(
                    gameView.getGameBall().getCenterX(),
                    gameView.getPlayerPaddle().getX(),
                    gameView.getPlayerPaddle().getWidth(),
                    gameModeManager.isRandomModeActive());

            if (hitFactor == 0) {
                ballTrajectoryAngle = Math.PI / 2;
            } else {
                ballTrajectoryAngle = hitFactor < 0 ? Math.PI - Math.max(hitFactor * -Math.PI / 2, Math.PI / 6)
                        : Math.max(hitFactor * Math.PI / 2, Math.PI / 6);
            }

            // 随机模式下添加额外随机角度
            if (gameModeManager.isRandomModeActive()) {
                ballTrajectoryAngle += (Math.random() * 0.3 - 0.15);
            }

            gameView.getGameBall().setCenterY(gameView.getPlayerPaddle().getY() - gameView.getGameBall().getRadius());
            scoreManager.updateScore();
        }
    }

    private void handleBrickCollisions() {
        boolean collisionDetected = false;
        double minDistance = Double.MAX_VALUE;
        int collisionRow = -1;
        int collisionCol = -1;

        // 找出距离球中心最近的砖块进行碰撞处理
        for (int i = 0; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
            for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                if (gameView.getGameBricks()[i][j].isVisible() &&
                        gameView.getGameBall().intersects(gameView.getGameBricks()[i][j].getBoundsInLocal()) &&
                        !Color.RED.equals(gameView.getGameBricks()[i][j].getFill())) {

                    double brickCenterX = gameView.getGameBricks()[i][j].getX() +
                            gameView.getGameBricks()[i][j].getWidth() / 2;
                    double brickCenterY = gameView.getGameBricks()[i][j].getY() +
                            gameView.getGameBricks()[i][j].getHeight() / 2;
                    double distance = Math.sqrt(
                            Math.pow(gameView.getGameBall().getCenterX() - brickCenterX, 2) +
                                    Math.pow(gameView.getGameBall().getCenterY() - brickCenterY, 2));

                    if (distance < minDistance) {
                        minDistance = distance;
                        collisionRow = i;
                        collisionCol = j;
                        collisionDetected = true;
                    }
                }
            }
        }

        // 处理碰撞
        if (collisionDetected) {
            double ballCenterX = gameView.getGameBall().getCenterX();
            double ballCenterY = gameView.getGameBall().getCenterY();
            double brickX = gameView.getGameBricks()[collisionRow][collisionCol].getX();
            double brickY = gameView.getGameBricks()[collisionRow][collisionCol].getY();
            double brickWidth = gameView.getGameBricks()[collisionRow][collisionCol].getWidth();
            double brickHeight = gameView.getGameBricks()[collisionRow][collisionCol].getHeight();

            // 判断是水平碰撞还是垂直碰撞
            double overlapLeft = ballCenterX + GameConstants.BALL_SIZE_RADIUS - brickX;
            double overlapRight = brickX + brickWidth - (ballCenterX - GameConstants.BALL_SIZE_RADIUS);
            double overlapTop = ballCenterY + GameConstants.BALL_SIZE_RADIUS - brickY;
            double overlapBottom = brickY + brickHeight - (ballCenterY - GameConstants.BALL_SIZE_RADIUS);

            // 找出最小重叠部分确定碰撞方向
            double minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

            if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                // 水平碰撞，左右反弹
                ballTrajectoryAngle = Math.PI - ballTrajectoryAngle;
            } else {
                // 垂直碰撞，上下反弹
                ballTrajectoryAngle = -ballTrajectoryAngle;
            }

            if (gameModeManager.isRandomModeActive()) {
                // 限制随机角度变化以防止极端反弹角度
                ballTrajectoryAngle += (Math.random() * 0.2 - 0.1);
            }

            // 防止球卡在砖块内
            if (minOverlap == overlapLeft) {
                gameView.getGameBall().setCenterX(brickX - GameConstants.BALL_SIZE_RADIUS - 1);
            } else if (minOverlap == overlapRight) {
                gameView.getGameBall().setCenterX(brickX + brickWidth + GameConstants.BALL_SIZE_RADIUS + 1);
            } else if (minOverlap == overlapTop) {
                gameView.getGameBall().setCenterY(brickY - GameConstants.BALL_SIZE_RADIUS - 1);
            } else if (minOverlap == overlapBottom) {
                gameView.getGameBall().setCenterY(brickY + brickHeight + GameConstants.BALL_SIZE_RADIUS + 1);
            }

            gameView.getGameBricks()[collisionRow][collisionCol].setVisible(false);
            scoreManager.updateScore();
        }
    }

    private double calculateHitFactor(double ballCenterX, double paddleX, double paddleWidth,
            boolean randomModeActive) {
        double hitFactor = (ballCenterX - paddleX) / paddleWidth - 0.5;
        if (randomModeActive) {
            hitFactor += (Math.random() - 0.5) / 2.0;
        }
        return hitFactor;
    }
}