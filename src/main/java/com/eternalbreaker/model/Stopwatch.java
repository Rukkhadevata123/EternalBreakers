package com.eternalbreaker.model;

public class Stopwatch {
    private long start;
    private long pausedTime;

    public Stopwatch() {
        start = System.currentTimeMillis();
        pausedTime = 0;
    }

    public void pause() {
        pausedTime = System.currentTimeMillis();
    }

    public void resume() {
        start += System.currentTimeMillis() - pausedTime;
        pausedTime = 0;
    }

    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    }
    
    // 添加一个方法判断秒表是否暂停
    public boolean isNotPaused() {
        return pausedTime <= 0;
    }
}