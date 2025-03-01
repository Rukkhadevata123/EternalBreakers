package com.eternalbreaker.controller;

import com.eternalbreaker.view.GameView;
import com.eternalbreaker.model.GameConstants;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class InputManager {
    private final GameView gameView;
    private final Scene scene; 
    
    public InputManager(GameView gameView, Scene scene) {
        this.gameView = gameView;
        this.scene = scene;
        initialize(); 
    }
    
    private void initialize() {
        scene.setOnMouseMoved(this::handleMouseMoved);
    }
    
    private void handleMouseMoved(MouseEvent e) {
        double mouseX = e.getX();
        double paddleWidth = gameView.getPlayerPaddle().getWidth();
        // 添加约束确保挡板不会移出游戏区域
        double newX = Math.max(0, Math.min(GameConstants.SCREEN_WIDTH - paddleWidth, 
                                           mouseX - paddleWidth / 2));
        gameView.getPlayerPaddle().setX(newX);
    }
}