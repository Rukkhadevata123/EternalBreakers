package com.eternalbreaker.controller;

import com.eternalbreaker.model.GameConstants;
import com.eternalbreaker.model.GameState;
import com.eternalbreaker.view.GameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.util.Duration;

public class GameController {
    private final GameView gameView;
    private final GameState gameState;
    private final Scene scene;

    private BallPhysicsManager ballPhysicsManager;
    private UIUpdateManager uiManager;
    private GameModeManager modeManager;
    private TimerManager timerManager;
    private ScoreManager scoreManager;
    private InputManager inputManager;

    private Timeline animation;

    private boolean gameIsRunning = false;
    private boolean resetInProgress = false;

    public GameController(GameView gameView, GameState gameState, Scene scene) {
        this.gameView = gameView;
        this.gameState = gameState;
        this.scene = scene;

        initialize();
    }

    private void initialize() {
        initializeManagers();
        setupGameLoop();
        setupButtonListeners();
        setupUIComponents();
    }

    private void initializeManagers() {
        // 创建所有管理器，解决循环依赖问题
        uiManager = new UIUpdateManager(gameView, gameState);
        modeManager = new GameModeManager(gameView, null, uiManager);
        scoreManager = new ScoreManager(gameView, gameState, modeManager, null);
        ballPhysicsManager = new BallPhysicsManager(gameView, scoreManager, modeManager);

        // 设置依赖关系
        modeManager.setPhysicsManager(ballPhysicsManager);
        scoreManager.setPhysicsManager(ballPhysicsManager);

        timerManager = new TimerManager(gameState, uiManager);
        inputManager = new InputManager(gameView, scene);
    }

    private void setupGameLoop() {
        animation = new Timeline(new KeyFrame(Duration.millis(10), e -> {
            try {
                gameLoop();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
    }

    private void setupUIComponents() {
        // 设置UI更新组件
        uiManager.setupScoreLabel();
        uiManager.setupRoundLabel();
        uiManager.setupTotalTimeUsedLabel();
        uiManager.setupLastRoundTimeUsedLabel();
    }

    // 添加新的方法来管理按钮状态
    private void updateButtonsState() {
        boolean settingsEnabled = !gameIsRunning;

        // 更新速度按钮
        gameView.getSlowSpeedButton().setDisable(!settingsEnabled);
        gameView.getNormalSpeedButton().setDisable(!settingsEnabled);
        gameView.getFastSpeedButton().setDisable(!settingsEnabled);

        // 更新挡板长度按钮
        gameView.getLongLengthButton().setDisable(!settingsEnabled);
        gameView.getNormalLengthButton().setDisable(!settingsEnabled);
        gameView.getShortLengthButton().setDisable(!settingsEnabled);

        // 更新游戏模式按钮
        gameView.getHellMButton().setDisable(!settingsEnabled);
        gameView.getRandomModeButton().setDisable(!settingsEnabled);
    }

    // 修改 setupButtonListeners 方法，为所有按钮添加游戏状态检查
    private void setupButtonListeners() {
        // 速度控制按钮
        gameView.getSlowSpeedButton().setOnAction(e -> {
            if (!gameIsRunning) {
                modeManager.setSlowSpeed();
            }
        });

        gameView.getNormalSpeedButton().setOnAction(e -> {
            if (!gameIsRunning) {
                modeManager.setNormalSpeed();
            }
        });

        gameView.getFastSpeedButton().setOnAction(e -> {
            if (!gameIsRunning) {
                modeManager.setFastSpeed();
            }
        });

        // 挡板长度控制按钮
        gameView.getLongLengthButton().setOnAction(e -> {
            if (!gameIsRunning) {
                modeManager.setLongPaddleLength();
            }
        });

        gameView.getNormalLengthButton().setOnAction(e -> {
            if (!gameIsRunning) {
                modeManager.setNormalPaddleLength();
            }
        });

        gameView.getShortLengthButton().setOnAction(e -> {
            if (!gameIsRunning) {
                modeManager.setShortPaddleLength();
            }
        });

        // 游戏模式按钮
        gameView.getHellMButton().setOnAction(e -> {
            if (!gameIsRunning) {
                modeManager.setHellMode();
            }
        });

        gameView.getRandomModeButton().setOnAction(e -> {
            if (!gameIsRunning) {
                boolean isActive = !modeManager.isRandomModeActive();
                modeManager.setRandomMode(isActive);
                if (isActive) {
                    uiManager.showAndHideMessage("Random mode activated! Speed and length will change randomly.", 3);
                    modeManager.changeSpeed();
                    modeManager.changeLength();
                } else {
                    uiManager.showAndHideMessage("Random mode deactivated!", 3);
                }
            }
        });

        // 开始按钮 - 保持现有逻辑
        gameView.getStartButton().setOnAction(e -> {
            if (!gameIsRunning && !resetInProgress) {
                startGame();
            } else if (gameIsRunning) {
                pauseGame();
            } else {
                resumeGame();
            }
        });

        // 初始更新按钮状态
        updateButtonsState();
    }

    private void gameLoop() throws InterruptedException {
        if (gameIsRunning) {
            // 移动球
            ballPhysicsManager.moveBall();

            // 检查是否所有砖块都被打破
            if (ballPhysicsManager.checkAllBricksBroken()) {
                handleAllBricksBroken();
            }

            // 检查球是否碰到底部墙壁
            if (ballPhysicsManager.checkBottomWallCollision()) {
                handleGameLost();
            }
        }
    }

    // 在startGame方法中添加按钮状态更新
    private void startGame() {
        // 游戏状态设置
        gameIsRunning = true;
        resetInProgress = false; // 修改为false，表示没有重置正在进行
        gameView.getStartButton().setText("Pause");

        // 禁用游戏设置按钮
        updateButtonsState();

        // 隐藏之前显示的游戏结束消息
        gameView.getStatusMessageLabel().setVisible(false);

        // 重置游戏状态
        resetGame();

        // 初始显示时间为0
        gameView.getTotalTimeUsedLabel().setText("Total time used: 0.0 seconds");
        gameView.getLastRoundTimeLabel().setText("Current round time: 0.0 seconds");

        // 开始倒计时，添加消息显示
        uiManager.showAndHideMessage("Get ready!", 1);
        gameState.setPreparationTimeLeft(3);
        timerManager.startGameCountdown(() -> {
            gameIsRunning = true;
            animation.play();
            // 启动计时器显示
            uiManager.startTimers();
            // 确保倒计时结束后按钮仍保持禁用
            updateButtonsState();
        });
    }

    // 添加新方法，合并resetBricks和resetBall，并添加其他重置逻辑
    private void resetGame() {
        resetBricks();
        resetBall();

        // 重置挡板位置
        gameView.getPlayerPaddle().setX((double) GameConstants.SCREEN_WIDTH / 2 -
                gameView.getPlayerPaddle().getWidth() / 2);

        gameState.setPlayerScore(0);
        gameState.setCurrentGameRound(1);

        gameState.setTotalElapsedTime(0);
        gameState.setLastRoundElapsedTime(0);

        // 不需要单独调用以下方法，因为UIUpdateManager已经设置了Timeline
        // updateScoreLabel();
        // updateRoundLabel();
        // updateTotalTimeUsedLabel();
        // updateLastRoundTimeUsedLabel();
    }

    // 在pauseGame方法中添加注释解释按钮状态
    private void pauseGame() {
        gameIsRunning = false;
        animation.pause();
        gameState.getGameRoundStopwatch().pause();
        gameState.getBrickResetStopwatch().pause(); // 确保两个秒表都暂停
        uiManager.pauseTimers(); // 暂停时间显示更新

        gameView.getStartButton().setText("Resume");
        uiManager.showAndHideMessage("Game paused", 999);

        // 保持按钮禁用状态，即使游戏暂停
        // 我们不希望玩家在暂停时修改设置
        // gameIsRunning为false但不更新按钮状态，确保暂停时不能修改设置
    }

    // 在resumeGame方法中添加按钮状态更新
    private void resumeGame() {
        gameIsRunning = true;
        gameState.getGameRoundStopwatch().resume();
        gameState.getBrickResetStopwatch().resume(); // 确保两个秒表都恢复
        animation.play();
        uiManager.startTimers(); // 恢复时间显示更新

        gameView.getStartButton().setText("Pause");
        uiManager.showAndHideMessage("Game resumed", 3);

        // 再次禁用设置按钮
        updateButtonsState();
    }

    // 添加调试输出到handleAllBricksBroken方法
    private void handleAllBricksBroken() {
        System.out.println("检查所有砖块是否被打破: resetInProgress = " + resetInProgress);

        if (!resetInProgress) {
            resetInProgress = true; // 设置标志，防止重复处理
            gameIsRunning = false;
            animation.pause();

            // 获取下一轮的轮数
            int nextRound = gameState.getCurrentGameRound() + 1;
            gameState.setCurrentGameRound(nextRound);

            // 确保消息显示明确且足够长时间可见
            String levelCompleteMessage = String.format("恭喜! 第 %d 轮完成!", gameState.getCurrentGameRound() - 1);
            uiManager.showAndHideMessage(levelCompleteMessage, 2);

            // 添加调试消息
            System.out.println("所有砖块已消除，准备进入第 " + nextRound + " 轮");

            // 等待短暂时间后开始倒计时，确保完成消息可见
            Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
                System.out.println("开始倒计时到下一轮...");
                timerManager.startResetCountdown(this::resetForNextRound);
            }));
            delayTimeline.play();
        }
    }

    // 在handleGameLost方法中添加按钮状态更新
    private void handleGameLost() {
        gameIsRunning = false;
        resetInProgress = false;
        animation.pause();
        uiManager.pauseTimers(); // 游戏结束时停止计时器显示更新

        // 启用游戏设置按钮
        updateButtonsState();

        // 创建包含得分信息的游戏结束消息
        String gameOverMessage = String.format("游戏结束! 最终得分: %d  通过轮数: %d\n总游戏时间: %.1f秒\n点击Start重新开始",
                gameState.getPlayerScore(),
                gameState.getCurrentGameRound() - 1,
                gameState.getTotalElapsedTime());

        // 显示游戏结束消息，持续时间更长
        uiManager.showAndHideMessage(gameOverMessage, 999); // 使用999表示持久显示，直到下一次开始
        gameView.getStartButton().setText("Start");

        // 重置游戏模式但保留分数和时间统计
        modeManager.resetGameModes();

        // 恢复挡板可见性
        gameView.getPlayerPaddle().setVisible(true);

        // 重置球的速度为正常速度
        ballPhysicsManager.setBallVelocity(GameConstants.BALL_SPEED_NORMAL);

        // ⚠️ 不要在这里重置分数和轮数！让玩家能看到自己的成绩
    }

    // 修改resetForNextRound方法，删除重复设置秒表的代码
    private void resetForNextRound() {
        System.out.println("重置下一轮游戏");

        // 重置砖块和球
        resetBricks();
        resetBall();

        // 重要：先将resetInProgress设置为false，表示重置过程已完成
        resetInProgress = false;

        // 然后才设置游戏运行状态
        gameIsRunning = true;

        // 恢复游戏动画
        animation.play();

        // 重新禁用按钮
        updateButtonsState();

        // 不需要在这里重置秒表，TimerManager.startResetCountdown中已经做了
        // gameState.setBrickResetStopwatch(new Stopwatch());

        // 如果是随机模式，更新速度和长度
        if (modeManager.isRandomModeActive()) {
            modeManager.changeSpeed();
            modeManager.changeLength();
        }

        // 显示新回合开始的消息
        uiManager.showAndHideMessage("开始第 " + gameState.getCurrentGameRound() + " 轮!", 2);
    }

    private void resetBricks() {
        // 重置所有砖块
        for (int i = 0; i < GameConstants.TOTAL_BRICK_ROWS; i++) {
            for (int j = 0; j < GameConstants.TOTAL_BRICK_COLUMNS; j++) {
                gameView.getGameBricks()[i][j].setVisible(true);
            }
        }
    }

    private void resetBall() {
        // 重置球的位置
        gameView.getGameBall().setCenterX(GameConstants.SCREEN_WIDTH / 2.0);
        gameView.getGameBall().setCenterY(GameConstants.SCREEN_HEIGHT / 2.0);
    }

    public void shutdown() {
        // 停止所有活动
        if (animation != null) {
            animation.stop();
        }

        timerManager.stopAllTimelines();
        uiManager.stopAllTimelines();
    }

}