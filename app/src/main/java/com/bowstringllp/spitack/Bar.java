package com.bowstringllp.spitack;

import android.os.CountDownTimer;

/**
 * Created by rishabhjain on 12/24/15.
 */
public class Bar {
    private int countdown;
    private int xStart;
    private int xEnd;
    private int width;
    private int yEnd = 5;

    public int getCountdown() {
        return countdown;
    }

    private int yTurning;
    private int addFactor = 10;

    public Bar() {
        countdown = MainActivity.getCountdownValue();

        new CountDownTimer(countdown * 800, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public synchronized   void onFinish() {
                countdown = 0;
            }
        }.start();
    }

    public int getAddFactor() {
        return addFactor;
    }

    public void setAddFactor(int addFactor) {
        this.addFactor = addFactor;
    }

    public int getxStart() {
        return xStart;

    }

    public void setxStart(int xStart) {
        this.xStart = xStart;
    }

    public int getxEnd() {
        return xEnd;
    }

    public void setxEnd(int xEnd) {
        this.xEnd = xEnd;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getyEnd() {
        return yEnd;
    }

    public void setyEnd(int yEnd) {
        this.yEnd = yEnd;
    }

    public int getyTurning() {
        return yTurning;
    }

    public void setyTurning(int yTurning) {
        this.yTurning = yTurning;
    }
}
