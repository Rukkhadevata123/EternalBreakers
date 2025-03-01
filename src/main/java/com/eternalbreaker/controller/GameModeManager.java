package com.eternalbreaker.controller;

import com.eternalbreaker.model.GameConstants;
import com.eternalbreaker.view.GameView;

import java.util.concurrent.ThreadLocalRandom;

public class GameModeManager {
    private final GameView gameView;
    private final UIUpdateManager uiManager;
    private BallPhysicsManager physicsManager;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private boolean hellModeActive = false;
    
    private boolean randomModeActive = false;
    
    public GameModeManager(GameView gameView, BallPhysicsManager physicsManager, UIUpdateManager uiManager) {
        this.gameView = gameView;
        this.physicsManager = physicsManager;
        this.uiManager = uiManager;
    }
    
    public void setPhysicsManager(BallPhysicsManager physicsManager) {
        this.physicsManager = physicsManager;
    }
    
    public boolean isRandomModeActive() {
        return randomModeActive;
    }

    public boolean isHellModeActive() {
        return hellModeActive;
    }
    
    public void setRandomMode(boolean active) {
        this.randomModeActive = active;
        if (active) {
            changeSpeed();
            changeLength();
        }
    }
    
    public void setHellMode() {
        physicsManager.setBallVelocity(GameConstants.BALL_SPEED_HELL);
        randomModeActive = false;
        hellModeActive = true;
        gameView.getPlayerPaddle().setWidth(GameConstants.PADDLE_LENGTH_NORMAL);
        gameView.getPlayerPaddle().setVisible(false);
        
        uiManager.updateSpeedLabel("Speed: HELL(" + GameConstants.BALL_SPEED_HELL + ")");
        uiManager.updatePaddleLengthLabel("Length: TRANSPARENT(" + GameConstants.PADDLE_LENGTH_NORMAL + ")");
    }
    
    public void setNormalSpeed() {
        if (hellModeActive) {
            // 退出地狱模式
            hellModeActive = false;
            gameView.getPlayerPaddle().setVisible(true);
        }
        
        if (!randomModeActive) {
            physicsManager.setBallVelocity(GameConstants.BALL_SPEED_NORMAL);
            uiManager.updateSpeedLabel("Speed: Normal(" + GameConstants.BALL_SPEED_NORMAL + ")");
        }
    }
    
    public void setSlowSpeed() {
        if (hellModeActive) {
            // 退出地狱模式
            hellModeActive = false;
            gameView.getPlayerPaddle().setVisible(true);
        }

        if (!randomModeActive || physicsManager.getBallVelocityX() != GameConstants.BALL_SPEED_HELL) {
            physicsManager.setBallVelocity(GameConstants.BALL_SPEED_SLOW);
            randomModeActive = false;
            uiManager.updateSpeedLabel("Speed: " + GameConstants.BALL_SPEED_SLOW);
        }
    }
    
    public void setFastSpeed() {
        if (hellModeActive) {
            // 退出地狱模式
            hellModeActive = false;
            gameView.getPlayerPaddle().setVisible(true);
        }

        if (!randomModeActive || physicsManager.getBallVelocityX() != GameConstants.BALL_SPEED_HELL) {
            physicsManager.setBallVelocity(GameConstants.BALL_SPEED_FAST);
            randomModeActive = false;
            uiManager.updateSpeedLabel("Speed: " + GameConstants.BALL_SPEED_FAST);
        }
    }
    
    public void setNormalPaddleLength() {
        if (hellModeActive) {
            // 退出地狱模式
            hellModeActive = false;
            gameView.getPlayerPaddle().setVisible(true);
        }

        if (!randomModeActive || physicsManager.getBallVelocityX() != GameConstants.BALL_SPEED_HELL) {
            gameView.getPlayerPaddle().setWidth(GameConstants.PADDLE_LENGTH_NORMAL);
            gameView.getPlayerPaddle().setVisible(true);
            randomModeActive = false;
            uiManager.updatePaddleLengthLabel("Length: " + GameConstants.PADDLE_LENGTH_NORMAL);
        }
    }
    
    public void setLongPaddleLength() {
        if (hellModeActive) {
            // 退出地狱模式
            hellModeActive = false;
            gameView.getPlayerPaddle().setVisible(true);
        }

        if (!randomModeActive || physicsManager.getBallVelocityX() != GameConstants.BALL_SPEED_HELL) {
            gameView.getPlayerPaddle().setWidth(GameConstants.PADDLE_LENGTH_LONG);
            gameView.getPlayerPaddle().setVisible(true);
            randomModeActive = false;
            uiManager.updatePaddleLengthLabel("Length: " + GameConstants.PADDLE_LENGTH_LONG);
        }
    }
    
    public void setShortPaddleLength() {
        if (hellModeActive) {
            // 退出地狱模式
            hellModeActive = false;
            gameView.getPlayerPaddle().setVisible(true);
        }

        if (!randomModeActive || physicsManager.getBallVelocityX() != GameConstants.BALL_SPEED_HELL) {
            gameView.getPlayerPaddle().setWidth(GameConstants.PADDLE_LENGTH_SHORT);
            gameView.getPlayerPaddle().setVisible(true);
            randomModeActive = false;
            uiManager.updatePaddleLengthLabel("Length: " + GameConstants.PADDLE_LENGTH_SHORT);
        }
    }
    
    public void changeSpeed() {
        if (!randomModeActive) return;
        
        int randomSpeed = random.nextInt(3);
        double speedValue;
        
        switch (randomSpeed) {
            case 0:
                speedValue = GameConstants.BALL_SPEED_SLOW * (1 + Math.random());
                physicsManager.setBallVelocity(speedValue);
                uiManager.updateSpeedLabel("Speed: Slow(" + String.format("%.1f", speedValue) + ")");
                break;
            case 1:
                speedValue = GameConstants.BALL_SPEED_NORMAL * (1 + Math.random());
                physicsManager.setBallVelocity(speedValue);
                uiManager.updateSpeedLabel("Speed: Normal(" + String.format("%.1f", speedValue) + ")");
                break;
            case 2:
                speedValue = GameConstants.BALL_SPEED_FAST * (1 + Math.random());
                physicsManager.setBallVelocity(speedValue);
                uiManager.updateSpeedLabel("Speed: Fast(" + String.format("%.1f", speedValue) + ")");
                break;
        }
    }
    
    public void changeLength() { 
        if (!randomModeActive) return;
        
        int randomLength = random.nextInt(3);
        double paddleLength;
        
        switch (randomLength) {
            case 0:
                paddleLength = GameConstants.PADDLE_LENGTH_SHORT * (1 + Math.random());
                gameView.getPlayerPaddle().setWidth(paddleLength);
                gameView.getPlayerPaddle().setVisible(true);
                uiManager.updatePaddleLengthLabel("Length: Short(" + GameConstants.PADDLE_LENGTH_SHORT + ")");
                break;
            case 1:
                paddleLength = GameConstants.PADDLE_LENGTH_NORMAL * (1 + Math.random());
                gameView.getPlayerPaddle().setWidth(paddleLength);
                gameView.getPlayerPaddle().setVisible(true);
                uiManager.updatePaddleLengthLabel("Length: Normal(" + GameConstants.PADDLE_LENGTH_NORMAL + ")");
                break;  
            case 2:
                paddleLength = GameConstants.PADDLE_LENGTH_LONG * (1 + Math.random());
                gameView.getPlayerPaddle().setWidth(paddleLength);
                gameView.getPlayerPaddle().setVisible(true);
                uiManager.updatePaddleLengthLabel("Length: Long(" + GameConstants.PADDLE_LENGTH_LONG + ")");
                break;
        }
    }

    // 在resetGameModes中重置地狱模式
    public void resetGameModes() {
        randomModeActive = false;
        hellModeActive = false;
        
        physicsManager.setBallVelocity(GameConstants.BALL_SPEED_NORMAL);
        gameView.getPlayerPaddle().setWidth(GameConstants.PADDLE_LENGTH_NORMAL);
        gameView.getPlayerPaddle().setVisible(true);
        
        uiManager.updateSpeedLabel("Speed: Normal(" + GameConstants.BALL_SPEED_NORMAL + ")");
        uiManager.updatePaddleLengthLabel("Length: Normal(" + GameConstants.PADDLE_LENGTH_NORMAL + ")");
    }
}