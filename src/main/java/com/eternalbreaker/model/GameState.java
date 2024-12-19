package com.eternalbreaker.model;

public class GameState {
    private int playerScore;
    private int currentGameRound;
    private double totalElapsedTime;
    private double lastRoundElapsedTime;
    private int preparationTimeLeft;
    private int resetCountdownTime;
    private Stopwatch gameRoundStopwatch;
    private Stopwatch brickResetStopwatch;

    public GameState() {
        this.playerScore = 0;
        this.currentGameRound = 1;
        this.totalElapsedTime = 0;
        this.lastRoundElapsedTime = 0;
        this.preparationTimeLeft = 0;
        this.resetCountdownTime = 0;
        this.gameRoundStopwatch = new Stopwatch();
        this.brickResetStopwatch = new Stopwatch();
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getCurrentGameRound() {
        return currentGameRound;
    }

    public void setCurrentGameRound(int currentGameRound) {
        this.currentGameRound = currentGameRound;
    }

    public double getTotalElapsedTime() {
        return totalElapsedTime;
    }

    public void setTotalElapsedTime(double totalElapsedTime) {
        this.totalElapsedTime = totalElapsedTime;
    }

    public double getLastRoundElapsedTime() {
        return lastRoundElapsedTime;
    }

    public void setLastRoundElapsedTime(double lastRoundElapsedTime) {
        this.lastRoundElapsedTime = lastRoundElapsedTime;
    }

    public int getPreparationTimeLeft() {
        return preparationTimeLeft;
    }

    public void setPreparationTimeLeft(int preparationTimeLeft) {
        this.preparationTimeLeft = preparationTimeLeft;
    }

    public Stopwatch getGameRoundStopwatch() {
        return gameRoundStopwatch;
    }

    public void setGameRoundStopwatch(Stopwatch gameRoundStopwatch) {
        this.gameRoundStopwatch = gameRoundStopwatch;
    }

    public Stopwatch getBrickResetStopwatch() {
        return brickResetStopwatch;
    }

    public void setBrickResetStopwatch(Stopwatch brickResetStopwatch) {
        this.brickResetStopwatch = brickResetStopwatch;
    }

    public int getResetCountdownTime() {
        return resetCountdownTime;
    }

    public void setResetCountdownTime(int resetCountdownTime) {
        this.resetCountdownTime = resetCountdownTime;
    }

}