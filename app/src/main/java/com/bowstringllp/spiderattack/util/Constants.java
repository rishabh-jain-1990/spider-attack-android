package com.bowstringllp.spiderattack.util;

/**
 * Created by rishabhjain on 7/19/16.
 */
public class Constants {
    public static final String BEE_BITMAP = "Bee bitmap key";
    public static final String SPIDER_BITMAP = "spider bitmap key";
    public static final long LEVEL_UP_TIME = 90;

    public static final int ROOKIE_TIME_THRESHOLD = 30;
    public static final int BEGINNER_TIME_THRESHOLD = 60;
    public static final int INTERMEDIATE_TIME_THRESHOLD = 120;
    public static final int EXPERT_TIME_THRESHOLD = 300;
    public static final int INVINCIBLE_TIME_THRESHOLD = 600;

    // Divide the spider width by this to get the spider speed
    public static final double SPIDER_SPEED_DIVIDER = 16.5;

    // Divide the minimum spider speed by this to get the increment to spider speed
    public static final double SPIDER_SPEED_STEP_DIVIDER = LEVEL_UP_TIME;

    // Divide the spider width by this to get the spider speed
    public static final double BEE_SPEED_DIVIDER = 13;
}