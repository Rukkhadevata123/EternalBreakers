package com.eternalbreaker.controller;

import com.eternalbreaker.model.GameState;
import com.eternalbreaker.view.GameView;
import com.eternalbreaker.model.GameConstants;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class UIUpdateManager {
    private final GameView gameView;
    private final GameState gameState;

    // 添加计时器控制方法
    private Timeline totalTimeUpdater;
    private Timeline lastRoundTimeUpdater;
    private boolean timersRunning = false;

    private Timeline statusMessageTimeline;
    private List<Timeline> uiTimelines = new ArrayList<>();

    public UIUpdateManager(GameView gameView, GameState gameState) {
        this.gameView = gameView;
        this.gameState = gameState;
    }

    public void startTimers() {
        if (!timersRunning) {
            timersRunning = true;
            if (totalTimeUpdater != null) totalTimeUpdater.play();
            if (lastRoundTimeUpdater != null) lastRoundTimeUpdater.play();
        }
    }
    
    public void pauseTimers() {
        if (timersRunning) {
            timersRunning = false;
            if (totalTimeUpdater != null) totalTimeUpdater.pause();
            if (lastRoundTimeUpdater != null) lastRoundTimeUpdater.pause();
        }
    }

    public void showAndHideMessage(String message, int duration) {
        if (statusMessageTimeline != null) {
            statusMessageTimeline.stop();
        }

        // 获取标签
        Label statusLabel = gameView.getStatusMessageLabel();

        // 设置自适应宽度 - 短消息窄一点，长消息宽一点
        double preferredWidth = Math.min(GameConstants.SCREEN_WIDTH * 0.8, Math.max(300, message.length() * 12));

        // 计算合适的阴影大小 - 短消息小阴影，长消息大阴影
        int shadowSize = Math.min(4 + message.length() / 20, 8);

        // 根据消息长度计算字体大小
        int fontSize = message.length() > 40 ? 14 : 16;

        // 创建高级样式 - 添加渐变背景和适合的阴影
        String style = String.format(
                "-fx-background-color: linear-gradient(to bottom, rgba(40, 40, 70, 0.9), rgba(20, 20, 40, 0.9));" +
                        "-fx-padding: 10px 15px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-color: rgba(80, 80, 150, 0.6);" +
                        "-fx-border-width: 1px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), %d, 0, 0, 2);" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: %dpx;" +
                        "-fx-font-family: 'Arial';",
                shadowSize, fontSize);

        // 应用样式
        statusLabel.setStyle(style);

        // 设置尺寸
        statusLabel.setPrefWidth(preferredWidth);
        statusLabel.setMaxWidth(GameConstants.SCREEN_WIDTH * 0.8);
        statusLabel.setMinWidth(200);

        // 设置文本属性
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setWrapText(true);
        statusLabel.setTextFill(Color.WHITE);

        // 设置文本对齐方式
        statusLabel.setAlignment(javafx.geometry.Pos.CENTER);
        statusLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // 设置位置
        statusLabel.setLayoutX((GameConstants.SCREEN_WIDTH - preferredWidth) / 2);
        statusLabel.setLayoutY(GameConstants.SCREEN_HEIGHT / 3);

        // 重写淡入淡出动画，使用单一Timeline和KeyValue
        statusMessageTimeline = new Timeline();

        // 设置初始不透明度
        statusLabel.setOpacity(0);

        // 添加关键帧
        statusMessageTimeline.getKeyFrames().addAll(
                // 淡入
                new KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(statusLabel.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(300),
                        new javafx.animation.KeyValue(statusLabel.opacityProperty(), 1)));

        // 只有非持久消息才添加淡出
        if (duration < 999) {
            statusMessageTimeline.getKeyFrames().addAll(
                    // 保持可见
                    new KeyFrame(Duration.seconds(duration - 0.5)),
                    // 淡出
                    new KeyFrame(Duration.seconds(duration),
                            new javafx.animation.KeyValue(statusLabel.opacityProperty(), 0)));

            // 动画结束后隐藏
            statusMessageTimeline.setOnFinished(e -> statusLabel.setVisible(false));
        }

        // 播放动画
        statusMessageTimeline.play();
    }

    public void updateSpeedLabel(String text) {
        gameView.getBallSpeedLabel().setText(text);
    }

    public void updatePaddleLengthLabel(String text) {
        gameView.getPaddleLengthLabel().setText(text);
    }

    public void setupScoreLabel() {
        Timeline scoreUpdater = new Timeline(new KeyFrame(Duration.millis(100),
                e -> gameView.getScoreDisplayLabel().setText("Score: " + gameState.getPlayerScore())));
        scoreUpdater.setCycleCount(Timeline.INDEFINITE);
        scoreUpdater.play();
        uiTimelines.add(scoreUpdater);
    }

    public void setupRoundLabel() {
        Timeline roundUpdater = new Timeline(new KeyFrame(Duration.millis(100),
                e -> gameView.getRoundDisplayLabel().setText("Round: " + gameState.getCurrentGameRound())));
        roundUpdater.setCycleCount(Timeline.INDEFINITE);
        roundUpdater.play();
        uiTimelines.add(roundUpdater);
    }

    public void setupTotalTimeUsedLabel() {
        totalTimeUpdater = new Timeline(new KeyFrame(Duration.millis(100),
                e -> {
                    double currentTotalTime = gameState.getTotalElapsedTime();
    
                    // 如果游戏正在运行，加上当前回合已经经过的时间
                    if (gameState.getGameRoundStopwatch() != null && !gameState.getGameRoundStopwatch().isPaused()) {
                        currentTotalTime += gameState.getGameRoundStopwatch().elapsedTime();
                    }
    
                    String formattedTime = String.format("%.1f", currentTotalTime);
                    gameView.getTotalTimeUsedLabel().setText("Total time used: " + formattedTime + " seconds");
                }));
        totalTimeUpdater.setCycleCount(Timeline.INDEFINITE);
        // 不要立即播放，等待游戏开始
        uiTimelines.add(totalTimeUpdater);
    }

    public void setupLastRoundTimeUsedLabel() {
        lastRoundTimeUpdater = new Timeline(new KeyFrame(Duration.millis(100),
                e -> {
                    double timeToDisplay;
    
                    // 如果当前回合正在进行，显示当前回合已用时间
                    if (gameState.getBrickResetStopwatch() != null && !gameState.getBrickResetStopwatch().isPaused()) {
                        timeToDisplay = gameState.getBrickResetStopwatch().elapsedTime();
                        gameView.getLastRoundTimeLabel()
                                .setText("Current round time: " + String.format("%.1f", timeToDisplay) + " seconds");
                    } else {
                        // 否则显示上一回合时间
                        timeToDisplay = gameState.getLastRoundElapsedTime();
                        gameView.getLastRoundTimeLabel().setText("Last round time: " + String.format("%.1f", timeToDisplay) + " seconds");
                    }
                }));
        lastRoundTimeUpdater.setCycleCount(Timeline.INDEFINITE);
        // 不要立即播放，等待游戏开始
        uiTimelines.add(lastRoundTimeUpdater);
    }

    public void stopAllTimelines() {
        if (statusMessageTimeline != null) {
            statusMessageTimeline.stop();
        }

        for (Timeline timeline : uiTimelines) {
            timeline.stop();
        }
    }
}