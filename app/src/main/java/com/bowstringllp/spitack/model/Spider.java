package com.bowstringllp.spitack.model;

import android.graphics.Bitmap;
import android.os.CountDownTimer;

import com.bowstringllp.spitack.MyApplication;
import com.bowstringllp.spitack.ui.activity.MainActivity;
import com.bowstringllp.spitack.util.Constants;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rishabhjain on 12/24/15.
 */
public class Spider {
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

    @Inject
    @Named(Constants.SPIDER_BITMAP)
    protected Bitmap[] bitmap;

    private int currentBitmapIndex;
    private final int FRAME_RATE = 3;
    private int currentFrame = 0;

    public Spider() {

        MyApplication.getInstance().getNetComponent().inject(this);
        currentBitmapIndex = 0;

        countdown = MainActivity.getCountdownValue();

        new CountDownTimer(countdown * 800, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public synchronized void onFinish() {
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
}
