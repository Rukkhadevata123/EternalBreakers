package com.eternalbreaker.view;

import com.eternalbreaker.model.GameConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 * 菜单面板管理类，负责创建和配置顶部菜单
 */
public class MenuPanel {
    private final GridPane menuPane;
    
    private final Button slowSpeedButton;
    private final Button normalSpeedButton;
    private final Button fastSpeedButton;
    private final Button longLengthButton;
    private final Button normalLengthButton;
    private final Button shortLengthButton;
    private final Button hellMButton;
    private final Button randomModeButton;
    private final Button startButton;
    
    public MenuPanel() {
        // 创建菜单面板
        menuPane = new GridPane();
        menuPane.setAlignment(Pos.CENTER);
        menuPane.setHgap(10);
        menuPane.setVgap(10);
        
        // 设置菜单栏背景
        BackgroundFill backgroundFill = new BackgroundFill(
                Color.rgb(40, 40, 60, 0.9),
                new CornerRadii(0),
                Insets.EMPTY);
        menuPane.setBackground(new Background(backgroundFill));
        
        // 创建菜单标签
        Label speedLabel = createMenuLabel("Ball Speed:");
        Label lengthLabel = createMenuLabel("Paddle Length:");
        Label modeLabel = createMenuLabel("Mode:");
        
        // 创建按钮
        slowSpeedButton = new Button("Slow(" + GameConstants.BALL_SPEED_SLOW + ")");
        normalSpeedButton = new Button("Normal(" + GameConstants.BALL_SPEED_NORMAL + ")");
        fastSpeedButton = new Button("Fast(" + GameConstants.BALL_SPEED_FAST + ")");
        longLengthButton = new Button("Long(" + GameConstants.PADDLE_LENGTH_LONG + ")");
        normalLengthButton = new Button("Normal(" + GameConstants.PADDLE_LENGTH_NORMAL + ")");
        shortLengthButton = new Button("Short(" + GameConstants.PADDLE_LENGTH_SHORT + ")");
        hellMButton = new Button("Hell Mode");
        randomModeButton = new Button("Random Mode");
        startButton = new Button("Start");
        
        // 设置按钮尺寸
        configureButtonSizes();
        
        // 应用按钮样式
        applyButtonStyles();
        
        // 添加组件到菜单面板
        addComponentsToMenuPane(speedLabel, lengthLabel, modeLabel);
    }
    
    private Label createMenuLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        return label;
    }
    
    private void configureButtonSizes() {
        slowSpeedButton.setPrefWidth(100);
        normalSpeedButton.setPrefWidth(100);
        fastSpeedButton.setPrefWidth(100);
        longLengthButton.setPrefWidth(100);
        normalLengthButton.setPrefWidth(100);
        shortLengthButton.setPrefWidth(100);
        startButton.setPrefWidth(100);
        hellMButton.setPrefWidth(100);
        randomModeButton.setPrefWidth(100);
    }
    
    private void applyButtonStyles() {
        UIStyles.applyButtonStyle(slowSpeedButton, "#3498db");
        UIStyles.applyButtonStyle(normalSpeedButton, "#2ecc71");
        UIStyles.applyButtonStyle(fastSpeedButton, "#e74c3c");
        UIStyles.applyButtonStyle(longLengthButton, "#9b59b6");
        UIStyles.applyButtonStyle(normalLengthButton, "#1abc9c");
        UIStyles.applyButtonStyle(shortLengthButton, "#f39c12");
        UIStyles.applyButtonStyle(hellMButton, "#c0392b");
        UIStyles.applyButtonStyle(randomModeButton, "#d35400");
        UIStyles.applyButtonStyle(startButton, "#27ae60");
    }
    
    private void addComponentsToMenuPane(Label speedLabel, Label lengthLabel, Label modeLabel) {
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
    }
    
    // Getter 方法
    public GridPane getMenuPane() {
        return menuPane;
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