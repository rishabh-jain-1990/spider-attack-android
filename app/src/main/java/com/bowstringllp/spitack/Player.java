package com.bowstringllp.spitack;

/**
 * Created by rishabhjain on 12/24/15.
 */
public class Player {
    private int xStart;
    private int xEnd;
    private int width = 70;
    private int yStart;
    private int yEnd;
    private int height = 150;
    private int addFactor;

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
}
