package com.eternalbreaker.controller;

import com.eternalbreaker.model.GameConstants;
import com.eternalbreaker.model.GameState;
import com.eternalbreaker.view.GameView;

public class ScoreManager {
    private final GameView gameView;
    private final GameState gameState;
    private final GameModeManager modeManager;
    private BallPhysicsManager physicsManager;
    
    public ScoreManager(GameView gameView, GameState gameState, 
                        GameModeManager modeManager, BallPhysicsManager physicsManager) {
        this.gameView = gameView;
        this.gameState = gameState;
        this.modeManager = modeManager;
        this.physicsManager = physicsManager;
    }

    public void setPhysicsManager(BallPhysicsManager physicsManager) {
        this.physicsManager = physicsManager;
    }
    
    public void updateScore() {
        if (modeManager.isRandomModeActive()) {
            gameState.setPlayerScore(gameState.getPlayerScore() + (int) (Math.random() * 10));
        }
        
        if (physicsManager.getBallVelocityX() == GameConstants.BALL_SPEED_HELL && 
            physicsManager.getBallVelocityY() == GameConstants.BALL_SPEED_HELL) {
            gameState.setPlayerScore(gameState.getPlayerScore() + 6);
        }
        
        if (!gameView.getPlayerPaddle().isVisible()) {
            gameState.setPlayerScore(gameState.getPlayerScore() + 6);
        }
        
        if (physicsManager.getBallVelocityX() != GameConstants.BALL_SPEED_HELL && 
            physicsManager.getBallVelocityY() != GameConstants.BALL_SPEED_HELL) {
            
            // 根据球速加分
            double ballSpeed = physicsManager.getBallVelocityX();
            if (ballSpeed == GameConstants.BALL_SPEED_SLOW) {
                gameState.setPlayerScore(gameState.getPlayerScore() + 1);
            } else if (ballSpeed == GameConstants.BALL_SPEED_NORMAL) {
                gameState.setPlayerScore(gameState.getPlayerScore() + 2);
            } else if (ballSpeed == GameConstants.BALL_SPEED_FAST) {
                gameState.setPlayerScore(gameState.getPlayerScore() + 3);
            }
            
            // 根据挡板长度加分
            double paddleWidth = gameView.getPlayerPaddle().getWidth();
            if (paddleWidth == GameConstants.PADDLE_LENGTH_LONG) {
                gameState.setPlayerScore(gameState.getPlayerScore() + 1);
            } else if (paddleWidth == GameConstants.PADDLE_LENGTH_NORMAL) {
                gameState.setPlayerScore(gameState.getPlayerScore() + 2);
            } else if (paddleWidth == GameConstants.PADDLE_LENGTH_SHORT) {
                gameState.setPlayerScore(gameState.getPlayerScore() + 3);
            }
        }
    }
}