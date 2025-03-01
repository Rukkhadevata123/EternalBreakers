package com.eternalbreaker.view;

import com.eternalbreaker.model.GameConstants;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 游戏元素管理类，负责创建和配置游戏中的视觉元素
 */
public class GameElements {
    private final Pane gamePane;
    
    // 颜色序列映射，用于随机排列颜色
    private final Integer[] colorMapping;

    public GameElements(Pane gamePane) {
        this.gamePane = gamePane;
        
        // 初始化颜色映射，并随机打乱
        colorMapping = new Integer[] {0, 1, 2, 3, 4, 5, 6};
        List<Integer> mappingList = Arrays.asList(colorMapping);
        Collections.shuffle(mappingList);
        mappingList.toArray(colorMapping);
    }

    /**
     * 创建游戏球
     */
    public Circle createBall() {
        Circle ball = new Circle(
                (double) GameConstants.SCREEN_WIDTH / 2,
                (double) GameConstants.SCREEN_HEIGHT / 2,
                GameConstants.BALL_SIZE_RADIUS);

        ball.setFill(Color.WHITE);

        gamePane.getChildren().add(ball);
        return ball;
    }

    /**
     * 创建玩家挡板
     */
    public Rectangle createPaddle() {
        Rectangle paddle = new Rectangle(
                (double) GameConstants.SCREEN_WIDTH / 2 - GameConstants.PADDLE_LENGTH_NORMAL / 2,
                GameConstants.PADDLE_START_Y,
                GameConstants.PADDLE_LENGTH_NORMAL,
                GameConstants.PADDLE_HEIGHT_CONSTANT);

        // 创建挡板渐变 - 使用更高对比度的颜色
        LinearGradient paddleGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 220, 100)), // 金色顶部
                new Stop(1, Color.rgb(230, 160, 20)) // 深金色底部
        );

        // 设置填充
        paddle.setFill(paddleGradient);

        // 移除圆角，避免碰撞检测问题
        paddle.setArcWidth(0);
        paddle.setArcHeight(0);

        gamePane.getChildren().add(paddle);
        return paddle;
    }

    /**
     * 创建游戏砖块
     */
    public Rectangle[][] createBricks() {
        Rectangle[][] bricks = new Rectangle[GameConstants.TOTAL_BRICK_ROWS][GameConstants.TOTAL_BRICK_COLUMNS];

        for (int i = 0; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
            for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                bricks[i][j] = new Rectangle(
                        j * GameConstants.BRICK_TILE_WIDTH + GameConstants.BRICK_TILE_GAP,
                        i * GameConstants.BRICK_TILE_HEIGHT + GameConstants.BRICK_TILE_GAP,
                        GameConstants.BRICK_TILE_WIDTH - 2 * GameConstants.BRICK_TILE_GAP,
                        GameConstants.BRICK_TILE_HEIGHT - 2 * GameConstants.BRICK_TILE_GAP);

                // 保留适度的圆角
                bricks[i][j].setArcWidth(6);
                bricks[i][j].setArcHeight(6);

                // 为砖块设置渐变色彩
                configureBrickColor(bricks[i][j], i, j);

                // 使用轻微边框而不是内发光，提高可见度
                bricks[i][j].setStroke(Color.rgb(255, 255, 255, 0.3));
                bricks[i][j].setStrokeWidth(1);

                gamePane.getChildren().add(bricks[i][j]);
            }
        }

        return bricks;
    }

    /**
     * 配置砖块颜色 - 使用随机排列的彩虹色系
     */
    private void configureBrickColor(Rectangle brick, int row, int column) {
        // 第一行特殊红色处理
        if (row == 0) {
            LinearGradient redGradient = new LinearGradient(
                    0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 60, 60)), // 明亮红色
                    new Stop(1, Color.rgb(180, 0, 0))   // 深红色
            );
            brick.setFill(redGradient);
            return;
        }
    
        // 为每行定义固定的鲜艳颜色
        Color topColor;     // 砖块顶部颜色(亮)
        Color bottomColor;  // 砖块底部颜色(暗)
    
        // 使用随机映射的颜色序列
        int colorIndex = colorMapping[(row - 1) % 7];
    
        // 根据映射后的索引选择颜色
        switch (colorIndex) {
            case 0: // 红色系
                topColor = Color.rgb(255, 70, 70);
                bottomColor = Color.rgb(200, 0, 0);
                break;
            case 1: // 橙色系
                topColor = Color.rgb(255, 140, 0);
                bottomColor = Color.rgb(200, 100, 0);
                break;
            case 2: // 黄色系
                topColor = Color.rgb(255, 230, 0);
                bottomColor = Color.rgb(220, 180, 0);
                break;
            case 3: // 绿色系
                topColor = Color.rgb(60, 230, 60);
                bottomColor = Color.rgb(0, 160, 0);
                break;
            case 4: // 青色系
                topColor = Color.rgb(0, 210, 210);
                bottomColor = Color.rgb(0, 150, 170);
                break;
            case 5: // 蓝色系
                topColor = Color.rgb(30, 100, 255);
                bottomColor = Color.rgb(0, 50, 180);
                break;
            case 6: // 紫色系
                topColor = Color.rgb(180, 60, 220);
                bottomColor = Color.rgb(130, 0, 150);
                break;
            default: // 粉色系 (安全防护)
                topColor = Color.rgb(255, 100, 200);
                bottomColor = Color.rgb(200, 50, 150);
                break;
        }
        
        // 根据列号略微调整颜色亮度，使同一行的砖块有细微差别
        int variation = (column % 4) * 15;
        
        // 对选中的颜色做微调部分
        if (column % 2 == 0) {
            // 偶数列略微增亮
            topColor = topColor.brighter();
            bottomColor = Color.rgb(
                    (int)(Math.min(bottomColor.getRed() * 255 + variation, 255)),
                    (int)(Math.min(bottomColor.getGreen() * 255 + variation, 255)),
                    (int)(Math.min(bottomColor.getBlue() * 255 + variation, 255))
            );
        } else {
            // 奇数列略微加深
            bottomColor = bottomColor.darker();
        }
    
        // 创建渐变
        LinearGradient brickGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, topColor),
                new Stop(1, bottomColor)
        );
        brick.setFill(brickGradient);
    
        // 设置边框，增强视觉效果
        if (column % 3 == 0) {
            // 每隔几列使用不同亮度的边框
            brick.setStroke(Color.WHITE.deriveColor(0, 1, 1, 0.4));
            brick.setStrokeWidth(1.2);
        } else {
            brick.setStroke(Color.WHITE.deriveColor(0, 1, 1, 0.25));
            brick.setStrokeWidth(1.0);
        }
    }
}