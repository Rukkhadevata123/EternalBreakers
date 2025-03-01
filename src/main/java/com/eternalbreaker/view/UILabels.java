package com.eternalbreaker.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * UI标签管理类，负责创建和配置游戏中的标签
 */
public class UILabels {
    private final Pane gamePane;
    
    public UILabels(Pane gamePane) {
        this.gamePane = gamePane;
    }
    
    /**
     * 创建并配置标签
     */
    public Label createLabel(String text, double x, double y, boolean visible) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setStyle(
                "-fx-background-color: rgba(40, 40, 60, 0.7);" +
                "-fx-padding: 6px 12px;" +
                "-fx-background-radius: 6px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 4, 0, 0, 1);" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-font-family: 'Arial';");
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setVisible(visible);
        gamePane.getChildren().add(label);
        return label;
    }
}