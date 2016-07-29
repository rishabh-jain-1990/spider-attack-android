package com.bowstringllp.spiderattack.model;

import android.graphics.Bitmap;

import com.bowstringllp.spiderattack.MyApplication;
import com.bowstringllp.spiderattack.util.Constants;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rishabhjain on 12/24/15.
 */
public class Bee {
    private final int playerAddFactor;
    private int xStart;
    private int xEnd;
    private int width;
    private int yStart;
    private int yEnd;
    private int height;
    private int addFactor;

    @Inject
    @Named(Constants.BEE_BITMAP)
    protected Bitmap[] bitmap;

    private int currentBitmapIndex;
    private final int FRAME_RATE = 3;
    private int currentFrame = 0;

    public Bee(int playerAddFactor) {
        MyApplication.getInstance().getNetComponent().inject(this);
        currentBitmapIndex = 0;
        this.playerAddFactor = playerAddFactor;
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

        height = (width * getCurrentBitmap().getHeight()) / getCurrentBitmap().getWidth();
    }

    public int getyStart() {
        return yStart;
    }

    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public int getyEnd() {
        return yEnd;
    }

    public void setyEnd(int yEnd) {
        this.yEnd = yEnd;
    }

    //
    public int getHeight() {
        return height;
    }

    public int getAddFactor() {
        return addFactor;
    }

//    public Bitmap[] getBitmap() {
//        return bitmap;
//    }

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

    public void moveLeft() {
        addFactor = playerAddFactor * -1;
    }

    public void moveRight() {
        addFactor = playerAddFactor * 1;
    }

    public void stopMoving() {
        addFactor = 0;
    }
}
