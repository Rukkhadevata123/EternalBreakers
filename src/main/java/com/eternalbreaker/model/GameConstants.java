package com.eternalbreaker.model;

public class GameConstants {

    // 屏幕尺寸
    public static final int SCREEN_WIDTH = 600;
    public static final int SCREEN_HEIGHT = 400;

    // 球的尺寸和初始角度
    public static final int BALL_SIZE_RADIUS = 10;

    // 挡板相关
    public static final int PADDLE_HEIGHT_CONSTANT = 10;
    public static final int PADDLE_START_Y = SCREEN_HEIGHT - 50;

    // 挡板长度设置
    public static final double PADDLE_LENGTH_LONG = 120;
    public static final double PADDLE_LENGTH_NORMAL = 80;
    public static final double PADDLE_LENGTH_SHORT = 40;

    // 砖块布局
    public static final int TOTAL_BRICK_ROWS = 5;
    public static final int TOTAL_BRICK_COLUMNS = 10;
    public static final int BRICK_TILE_WIDTH = SCREEN_WIDTH / TOTAL_BRICK_COLUMNS;
    public static final int BRICK_TILE_HEIGHT = 15;
    public static final int BRICK_TILE_GAP = 4;

    // 球速度设置
    public static final double BALL_SPEED_SLOW = 5;
    public static final double BALL_SPEED_NORMAL = 6;
    public static final double BALL_SPEED_FAST = 7;
    public static final double BALL_SPEED_HELL = 12;

}