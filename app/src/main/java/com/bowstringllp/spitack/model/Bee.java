package com.bowstringllp.spitack.model;

import android.graphics.Bitmap;

import com.bowstringllp.spitack.MyApplication;
import com.bowstringllp.spitack.util.Constants;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rishabhjain on 12/24/15.
 */
public class Bee {
    private int xStart;
    private int xEnd;
    private int width = 70;
    private int yStart;
    private int yEnd;
    private int height = 150;
    private int addFactor;

    @Inject
    @Named(Constants.BEE_BITMAP)
    protected Bitmap[] bitmap;

    private int currentBitmapIndex;
    private final int FRAME_RATE = 3;
    private int currentFrame = 0;

    public Bee() {
        MyApplication.getInstance().getNetComponent().inject(this);
        currentBitmapIndex = 0;
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAddFactor() {
        return addFactor;
    }

    public void setAddFactor(int addFactor) {
        this.addFactor = addFactor;
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
}
