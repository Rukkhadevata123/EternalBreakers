package com.eternalbreaker.view;

import javafx.scene.control.Button;

/**
 * UI样式工具类，提供应用样式的静态方法
 */
public class UIStyles {
    /**
     * 应用按钮样式并添加悬停效果
     */
    public static void applyButtonStyle(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 5px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 3, 0, 0, 1);");

        // 添加鼠标悬停效果
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: derive(" + color + ", 20%);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 5px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 5, 0, 0, 2);"));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-background-radius: 5px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 3, 0, 0, 1);"));
    }
}