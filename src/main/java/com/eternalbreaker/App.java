package com.eternalbreaker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.eternalbreaker.view.GameView;
import com.eternalbreaker.controller.GameController;
import com.eternalbreaker.model.GameState;
import com.eternalbreaker.model.GameConstants;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameState gameState = new GameState();
        GameView gameView = new GameView();
        Scene scene = new Scene(gameView.getMainPane(), GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT + 60);
        GameController _gameController = new GameController(gameView, gameState, scene);

        primaryStage.setTitle("Eternal Breaker");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}