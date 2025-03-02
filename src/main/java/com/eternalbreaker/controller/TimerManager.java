package com.eternalbreaker.controller;

import com.eternalbreaker.model.GameState;
import com.eternalbreaker.model.Stopwatch;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TimerManager {
    private final GameState gameState;
    private final UIUpdateManager uiManager;

    private Timeline gameStartCountdownTimeline;
    private Timeline resetCountdownTimeline;

    public TimerManager(GameState gameState, UIUpdateManager uiManager) {
        this.gameState = gameState;
        this.uiManager = uiManager;
    }

    public void startGameCountdown(Runnable onComplete) {
        gameState.setPreparationTimeLeft(3);

        gameStartCountdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (gameState.getPreparationTimeLeft() > 0) {
                String message = "Game starting in " + gameState.getPreparationTimeLeft() + " seconds...";
                uiManager.showAndHideMessage(message, 1);
                gameState.setPreparationTimeLeft(gameState.getPreparationTimeLeft() - 1);
            } else {
                uiManager.showAndHideMessage("Game started!", 3);
                gameState.setGameRoundStopwatch(new Stopwatch());
                gameState.setBrickResetStopwatch(new Stopwatch());
                gameState.setPreparationTimeLeft(0);
                gameStartCountdownTimeline.stop();

                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }));

        gameStartCountdownTimeline.setCycleCount(4);
        gameStartCountdownTimeline.play();
    }

    public void startResetCountdown(Runnable onComplete) {
        // 设置倒计时时间
        gameState.setResetCountdownTime(3);

        // 先清除可能存在的旧倒计时
        if (resetCountdownTimeline != null) {
            resetCountdownTimeline.stop();
        }

        // 记录当前轮次时间
        gameState.getGameRoundStopwatch().pause();
        gameState
                .setTotalElapsedTime(gameState.getTotalElapsedTime() + gameState.getGameRoundStopwatch().elapsedTime());
        gameState.setLastRoundElapsedTime(gameState.getBrickResetStopwatch().elapsedTime());

        // 创建一个临时秒表来记录倒计时期间的时间
        Stopwatch countdownStopwatch = new Stopwatch();

        // 创建倒计时动画
        resetCountdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (gameState.getResetCountdownTime() > 0) {
                // 确保倒计时消息更明显
                String message = "下一关将在 " + gameState.getResetCountdownTime() + " 秒后开始...";
                uiManager.showAndHideMessage(message, 1);
                gameState.setResetCountdownTime(gameState.getResetCountdownTime() - 1);
            } else {
                // 倒计时结束
                resetCountdownTimeline.stop();

                // 将倒计时期间的时间加入到总时间中
                gameState.setTotalElapsedTime(
                        gameState.getTotalElapsedTime() + countdownStopwatch.elapsedTime());

                // 显示新一轮开始消息
                uiManager.showAndHideMessage("第 " + gameState.getCurrentGameRound() + " 轮开始!", 2);

                // 重置当前回合的秒表准备新一轮记时
                gameState.setGameRoundStopwatch(new Stopwatch());
                gameState.setBrickResetStopwatch(new Stopwatch());

                // 确保回调被执行
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }));

        resetCountdownTimeline.setCycleCount(4); // 3秒倒计时+1次结束
        resetCountdownTimeline.play();
    }

    public void stopAllTimelines() {
        if (gameStartCountdownTimeline != null) {
            gameStartCountdownTimeline.stop();
        }

        if (resetCountdownTimeline != null) {
            resetCountdownTimeline.stop();
        }

        // 清除可能的遗留消息
        // 注意：需要在UIUpdateManager中添加hideMessage方法
        uiManager.hideMessage();
    }
}