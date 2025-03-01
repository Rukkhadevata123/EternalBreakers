package com.eternalbreaker.view;

import com.eternalbreaker.model.GameConstants;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * 游戏背景管理类，负责创建和配置游戏背景
 */
public class GameBackground {
    private final Pane gamePane;
    
    public GameBackground(Pane gamePane) {
        this.gamePane = gamePane;
        setupGameBackground();
    }
    
    /**
     * 创建游戏背景，包括渐变背景和星星
     */
    private void setupGameBackground() {
        // 创建渐变背景 - 使用更深的颜色增强对比度
        Rectangle background = new Rectangle(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(30, 30, 60, 1.0)), // 更深的颜色
                new Stop(1, Color.rgb(10, 10, 25, 1.0))  // 几乎黑色
        );
        background.setFill(gradient);

        // 添加到游戏面板的最底层
        gamePane.getChildren().add(0, background);

        // 添加装饰性元素 - 背景星星
        // createStars();
    }
    
    /**
     * 创建背景星星效果
     */
    private void createStars() {
        for (int i = 0; i < 80; i++) {
            double size = Math.random() * 2.5 + 0.8;
            Circle star = new Circle(
                    Math.random() * GameConstants.SCREEN_WIDTH,
                    Math.random() * GameConstants.SCREEN_HEIGHT,
                    size);

            // 随机星星颜色：白色、淡蓝色或淡黄色
            Color starColor;
            int colorChoice = (int) (Math.random() * 3);
            switch (colorChoice) {
                case 0:
                    starColor = Color.WHITE;
                    break;
                case 1:
                    starColor = Color.rgb(200, 220, 255); // 淡蓝
                    break;
                default:
                    starColor = Color.rgb(255, 255, 220); // 淡黄
                    break;
            }

            star.setFill(starColor);
            star.setOpacity(Math.random() * 0.5 + 0.5); // 增加整体亮度
            gamePane.getChildren().add(1, star);
        }
    }
}