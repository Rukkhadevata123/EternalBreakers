package com.eternalbreaker.view;

import com.eternalbreaker.model.GameConstants;
import com.eternalbreaker.model.GameVersion;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 * 游戏视图的主类，管理所有视图组件
 */
public class GameView {
    // 视图层组件
    private final BorderPane mainPane;
    private final Pane gamePane;
    
    // 游戏元素
    private final Circle gameBall;
    private final Rectangle playerPaddle;
    private final Rectangle[][] gameBricks;
    
    // 游戏状态标签
    private final Label scoreDisplayLabel;
    private final Label roundDisplayLabel;
    private final Label statusMessageLabel;
    private final Label ballSpeedLabel;
    private final Label paddleLengthLabel;
    private final Label totalTimeUsedLabel;
    private final Label lastRoundTimeLabel;
    private final Label versionLabel;
    
    // 控制按钮
    private final Button slowSpeedButton;
    private final Button normalSpeedButton;
    private final Button fastSpeedButton;
    private final Button longLengthButton;
    private final Button normalLengthButton;
    private final Button shortLengthButton;
    private final Button hellMButton;
    private final Button randomModeButton;
    private final Button startButton;

    public GameView() {
        // 创建主面板
        mainPane = new BorderPane();
        
        // 创建游戏面板
        gamePane = new Pane();
        mainPane.setCenter(gamePane);
        
        // 创建和配置菜单面板
        MenuPanel menuPanel = new MenuPanel();
        mainPane.setTop(menuPanel.getMenuPane());
        
        // 获取菜单按钮引用
        slowSpeedButton = menuPanel.getSlowSpeedButton();
        normalSpeedButton = menuPanel.getNormalSpeedButton();
        fastSpeedButton = menuPanel.getFastSpeedButton();
        longLengthButton = menuPanel.getLongLengthButton();
        normalLengthButton = menuPanel.getNormalLengthButton();
        shortLengthButton = menuPanel.getShortLengthButton();
        hellMButton = menuPanel.getHellMButton();
        randomModeButton = menuPanel.getRandomModeButton();
        startButton = menuPanel.getStartButton();
        
        // 创建游戏背景
        GameBackground background = new GameBackground(gamePane);
        
        // 创建游戏元素
        GameElements elements = new GameElements(gamePane);
        gameBall = elements.createBall();
        playerPaddle = elements.createPaddle();
        gameBricks = elements.createBricks();
        
        // 创建游戏状态标签
        UILabels labels = new UILabels(gamePane);
        scoreDisplayLabel = labels.createLabel("Score: 0", 10, 100, true);
        roundDisplayLabel = labels.createLabel("Round: 1", 10, 130, true);
        lastRoundTimeLabel = labels.createLabel("Time used in the last round: 0.0 seconds", 10, 170, true);
        totalTimeUsedLabel = labels.createLabel("Time used in all rounds: 0.0 seconds", 10, 200, true);
        ballSpeedLabel = labels.createLabel("Speed: Normal(" + GameConstants.BALL_SPEED_NORMAL + ")", 10, 240, true);
        paddleLengthLabel = labels.createLabel("Length: Normal(" + GameConstants.PADDLE_LENGTH_NORMAL + ")", 10, 270, true);
        statusMessageLabel = labels.createLabel("Get ready!", (double) (GameConstants.SCREEN_WIDTH - 100) / 2, 
                (double) GameConstants.SCREEN_HEIGHT / 2 - 100, false);
        
        // 添加版本标签
        versionLabel = labels.createLabel("Version: " + GameVersion.VERSION, 10, 310, true);
    }

    // Getter 方法不变，保留所有原有的getter
    public BorderPane getMainPane() {
        return mainPane;
    }

    public Circle getGameBall() {
        return gameBall;
    }

    public Rectangle getPlayerPaddle() {
        return playerPaddle;
    }

    public Rectangle[][] getGameBricks() {
        return gameBricks;
    }

    public Label getScoreDisplayLabel() {
        return scoreDisplayLabel;
    }

    public Label getRoundDisplayLabel() {
        return roundDisplayLabel;
    }

    public Label getStatusMessageLabel() {
        return statusMessageLabel;
    }

    public Label getBallSpeedLabel() {
        return ballSpeedLabel;
    }

    public Label getPaddleLengthLabel() {
        return paddleLengthLabel;
    }

    public Label getTotalTimeUsedLabel() {
        return totalTimeUsedLabel;
    }

    public Label getLastRoundTimeLabel() {
        return lastRoundTimeLabel;
    }

    public Label getVersionLabel() {
        return versionLabel;
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