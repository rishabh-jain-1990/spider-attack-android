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
    private final int spiderAddFactor;
    private int countdown;
    private int xStart;
    private int xEnd;
    private int yEnd = 5;

    public int getCountdown() {
        return countdown;
    }

    private int yTurning;
    private int addFactor;

    @Inject
    @Named(Constants.SPIDER_BITMAP)
    protected Bitmap[] bitmap;

    private int currentBitmapIndex;
    private final int FRAME_RATE = 3;
    private int currentFrame = 0;

    private CountDownTimer timer;

    public Spider(int spiderAddFactor) {

        MyApplication.getInstance().getNetComponent().inject(this);
        currentBitmapIndex = 0;

        this.spiderAddFactor = addFactor = spiderAddFactor;
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

    public int getAddFactor() {
        return addFactor;
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
        addFactor = spiderAddFactor * -1;
    }

    public void moveDown() {
        addFactor = spiderAddFactor;
    }

    public void stopMoving() {
        addFactor = 0;
    }
}
