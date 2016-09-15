package com.bowstringllp.spiderattack.model;

import android.graphics.Bitmap;
import android.os.CountDownTimer;

import com.bowstringllp.spiderattack.MyApplication;
import com.bowstringllp.spiderattack.ui.activity.GameActivity;
import com.bowstringllp.spiderattack.util.Constants;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rishabhjain on 12/24/15.
 */
public class Spider {
    private final double minAddFactorValue;
    private double currentAddFactorValue;
    private double maxAddFactorValue;
    private int countdown;
    private double xStart;
    private double xEnd;
    private double yEnd = 5;

    public int getCountdown() {
        return countdown;
    }

    private int yTurning;
    private double currentAddFactor;

    @Inject
    @Named(Constants.SPIDER_BITMAP)
    protected Bitmap[] bitmap;

    private int currentBitmapIndex;
    private final int FRAME_RATE = 3;
    private int currentFrame = 0;

    private CountDownTimer timer;

    public Spider(double minAddFactorValue) {

        MyApplication.getInstance().getNetComponent().inject(this);
        currentBitmapIndex = 0;

        this.minAddFactorValue = currentAddFactor = currentAddFactorValue = minAddFactorValue;
        maxAddFactorValue = minAddFactorValue * 2;

        countdown = GameActivity.getCountdownValue() * 800;

        timer = new CountDownTimer(countdown * 800, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                countdown -= 100;
            }

            @Override
            public synchronized void onFinish() {
                countdown = 0;
            }
        }.start();
    }

    public double getCurrentAddFactor() {
        return currentAddFactor;
    }

    public double getxStart() {
        return xStart;

    }

    public void setxStart(double xStart) {
        this.xStart = xStart;
    }

    public double getxEnd() {
        return xEnd;
    }

    public void setxEnd(double xEnd) {
        this.xEnd = xEnd;
    }

    public double getyEnd() {
        return yEnd;
    }

    public void setyEnd(double yEnd) {
        this.yEnd = yEnd;
    }

    public int getyTurning() {
        return yTurning;
    }

    public void setyTurning(int yTurning) {
        this.yTurning = yTurning;
    }

    public Bitmap getCurrentBitmap() {
        return bitmap[currentBitmapIndex];
    }

    public void nextBitmap() {

//        if (currentFrame < FRAME_RATE)
//            currentFrame++;
//        else {
        currentFrame = 0;
        currentBitmapIndex++;

        if (currentBitmapIndex > 23)
            currentBitmapIndex = 0;
//        }
    }

    public void previousBitmap() {
//        if (currentFrame < FRAME_RATE)
//            currentFrame++;
//        else {
        currentFrame = 0;
        currentBitmapIndex--;

        if (currentBitmapIndex < 0)
            currentBitmapIndex = 23;
//        }
    }

    public void pause() {
        timer.cancel();
    }

    public void unPause() {
        timer = new CountDownTimer(countdown * 800, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                countdown -= 100;
            }

            @Override
            public synchronized void onFinish() {
                countdown = 0;
            }
        }.start();
    }


    public void moveUp() {
        currentAddFactor = currentAddFactorValue * -1;
    }

    public void moveDown() {
        currentAddFactor = currentAddFactorValue;
    }

    public void stopMoving() {
        currentAddFactor = 0;
    }

    public void speedUp() {
        if (currentAddFactorValue > 0)
            currentAddFactorValue += (minAddFactorValue / (Constants.SPIDER_SPEED_STEP_DIVIDER * Constants.SPIDER_SPEED_STEP_DURATION));
        else
            currentAddFactorValue -= (minAddFactorValue / (Constants.SPIDER_SPEED_STEP_DIVIDER * Constants.SPIDER_SPEED_STEP_DURATION));

        if (currentAddFactorValue >= maxAddFactorValue)
            currentAddFactorValue = maxAddFactorValue;
    }

    public void speedDown() {
        if (currentAddFactorValue < 0)
            currentAddFactorValue += (minAddFactorValue / (Constants.SPIDER_SPEED_STEP_DIVIDER * Constants.SPIDER_SPEED_STEP_DURATION));
        else
            currentAddFactorValue -= (minAddFactorValue / (Constants.SPIDER_SPEED_STEP_DIVIDER * Constants.SPIDER_SPEED_STEP_DURATION));

        if (currentAddFactorValue <= minAddFactorValue)
            currentAddFactorValue = minAddFactorValue;
    }

    public void speedReset() {
        currentAddFactorValue = minAddFactorValue;
    }
}
