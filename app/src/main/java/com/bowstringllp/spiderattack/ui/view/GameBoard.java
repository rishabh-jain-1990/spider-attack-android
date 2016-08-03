package com.bowstringllp.spiderattack.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.bowstringllp.spiderattack.R;
import com.bowstringllp.spiderattack.model.Bee;
import com.bowstringllp.spiderattack.model.Spider;
import com.bowstringllp.spiderattack.ui.activity.GameActivity;

import java.util.Random;

/**
 * Created by rishabhjain on 10/31/15.
 */
public class GameBoard extends View {

    private final Paint p;
    private final int NUM_OF_STARS;
    private final NinePatchDrawable threadImage;
    //    private final int spiderImageWidth;
    //  private final int spiderImageHeight;
//    private final int playerImageWidth;
    //  private final int playerImageHeight;
//    private final Bitmap spiderImage;
    //   private final Bitmap threadImageBitmap;
    private int barWidth;
    private int yPosition = 5;
    private int safeBarFirst = 0;
    private int safeBarSecond = 1;
    private Spider[] spiderArray;
    private boolean collisionDetected = false;
    private Point lastCollision = new Point(-1, -1);
    private Bee bee;
    private int measuredSpiderHeight;

    public Bee getBee() {
        return bee;
    }

    public Spider[] getSpiderArray() {
        return spiderArray;
    }

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        //it's best not to create any new objects in the on draw
        //initialize them as class variables here
        p = new Paint();
        NUM_OF_STARS = GameActivity.getNoOfStars();
        spiderArray = new Spider[NUM_OF_STARS];
        threadImage = (NinePatchDrawable) getResources().getDrawable(R.drawable.thread);
        //       threadImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thread_bit);
        //      spiderImage = BitmapFactory.decodeResource(getResources(), R.drawable.spider_00000);
        //       spiderImageWidth = spiderImage.getWidth();
        //     spiderImageHeight = spiderImage.getHeight();
//        playerImageWidth = bee.getCurrentBitmap().getWidth();
        //      playerImageHeight = bee.getCurrentBitmap().getHeight();
    }

    //return the point of the last collision
    synchronized public Point getLastCollision() {
        return lastCollision;
    }

    //return the collision flag
    synchronized public boolean wasCollisionDetected() {
        return collisionDetected;
    }

    private boolean checkForCollision() {

        Rect r1 = new Rect(bee.getxStart(), bee.getyStart(), bee.getxEnd(), bee.getyEnd());
        Rect r3 = new Rect(r1);

        for (int h = 0; h < NUM_OF_STARS; h++) {
            if (bee.getxStart() > spiderArray[h].getxEnd())
                continue;
            else if (bee.getxEnd() < spiderArray[h].getxStart())
                continue;

            Rect r2 = new Rect(spiderArray[h].getxStart(), spiderArray[h].getyEnd() - measuredSpiderHeight, spiderArray[h].getxEnd(), spiderArray[h].getyEnd());
            if (r1.intersect(r2)) {
                for (float i = r1.left; i < r1.right; i++) {
                    for (float j = r1.top; j < r1.bottom; j++) {
                        try {
                            int scaledSpiderX = (int) ((i - r2.left) / r2.width() * spiderArray[h].getCurrentBitmap().getWidth());
                            int scaledSpiderY = (int) ((j - r2.top) / r2.height() * spiderArray[h].getCurrentBitmap().getHeight());
                            int scaledPlayerX = (int) ((i - r3.left) / r3.width() * bee.getCurrentBitmap().getWidth());
                            int scaledPlayerY = (int) ((j - r3.top) / r3.height() * bee.getCurrentBitmap().getHeight());

                            if (bee.getCurrentBitmap().getPixel(scaledPlayerX, scaledPlayerY) !=
                                    Color.TRANSPARENT) {
                                if (spiderArray[h].getCurrentBitmap().getPixel(scaledSpiderX, scaledSpiderY) !=
                                        Color.TRANSPARENT) {
                                    lastCollision = new Point(spiderArray[h].getxStart() +
                                            (int) i - r2.left, (int) (0 + j - r2.top));
                                    return true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;
            }
        }
        lastCollision = new Point(-1, -1);
        return false;
    }

    public boolean isCollisionDetected() {
        return collisionDetected;
    }

//    public void setCollisionDetected(boolean collisionDetected) {
//        this.collisionDetected = collisionDetected;
//    }

    @Override
    synchronized public void onDraw(Canvas canvas) {

        //create a black canvas
        p.setColor(Color.BLACK);
        p.setAlpha(0);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        p.setAlpha(255);
        if (barWidth == 0)
            barWidth = getWidth() / NUM_OF_STARS;

        if (bee == null)
            bee = new Bee(barWidth / 12);

        if (bee.getyEnd() == 0) {
            bee.setyEnd(getHeight());
            bee.setWidth((int) (barWidth / 2.7));
            bee.setyStart(getHeight() - bee.getHeight());
            bee.setxStart(getWidth() / 2);
        }

        p.setColor(Color.CYAN);
        //draw the stars
        p.setStrokeWidth(barWidth);

        for (int i = 0; i < NUM_OF_STARS; i++) {
            if (spiderArray[i] == null)
                spiderArray[i] = new Spider(barWidth / 12);

            if (measuredSpiderHeight == 0)
                measuredSpiderHeight = (barWidth * spiderArray[i].getCurrentBitmap().getHeight()) / spiderArray[i].getCurrentBitmap().getWidth();

            spiderArray[i].setxStart(barWidth * i);
            //  spiderArray[i].setWidth(barWidth);
            spiderArray[i].setxEnd(spiderArray[i].getxStart() + barWidth);

            if (spiderArray[i].getCountdown() > 0) {
                continue;
            }

            if (spiderArray[i].getyEnd() >= getHeight()) {
                spiderArray[i].moveUp();
                spiderArray[i].setyTurning(measuredSpiderHeight + new Random().nextInt((getHeight() / 2) - measuredSpiderHeight));
            } else if (spiderArray[i].getyEnd() <= spiderArray[i].getyTurning()) {
                spiderArray[i].moveDown();
//                safeBarFirst = new Random().nextInt(NUM_OF_STARS);
//                do {
//                    safeBarSecond = new Random().nextInt(NUM_OF_STARS);
//                } while (safeBarFirst == safeBarSecond);
            }

            spiderArray[i].nextBitmap();
            spiderArray[i].setyEnd(spiderArray[i].getyEnd() + spiderArray[i].getAddFactor());

            Rect rect = new Rect(spiderArray[i].getxStart() + (barWidth / 2) - (barWidth / 100), 0, spiderArray[i].getxEnd() - (barWidth / 2) + (barWidth / 100), spiderArray[i].getyEnd() - measuredSpiderHeight + (measuredSpiderHeight / 3));
            p.setColor(Color.BLACK);
            canvas.drawRect(rect, p);

            rect = new Rect(spiderArray[i].getxStart(), spiderArray[i].getyEnd() - measuredSpiderHeight, spiderArray[i].getxEnd(), spiderArray[i].getyEnd());
            canvas.drawBitmap(spiderArray[i].getCurrentBitmap(), null, rect, p);

            //canvas.drawBitmap(threadImageBitmap, null, rect, p);
//            threadImage.setBounds(rect);
//            threadImage.draw(canvas);
            //canvas.drawBitmap(spiderImage, null, rect, p);

        }

        if ((bee.getxEnd() + bee.getAddFactor()) <= getWidth() && (bee.getxStart() + bee.getAddFactor()) >= 0) {
            bee.setxStart(bee.getxStart() + bee.getAddFactor());
            bee.setxEnd(bee.getxStart() + bee.getWidth());
        }

        p.setColor(Color.MAGENTA);
        //draw the stars
        p.setStrokeWidth(bee.getWidth());

        Rect rect = new Rect(bee.getxStart(), bee.getyStart(), bee.getxEnd(), bee.getyEnd());
        if (bee.getAddFactor() > 0)
            bee.nextBitmap();
        else if (bee.getAddFactor() < 0)
            bee.previousBitmap();

        canvas.drawBitmap(bee.getCurrentBitmap(), null, rect, p);
//        for (int i = 0; i < NUM_OF_STARS; i++) {
//            if (i == safeBarFirst || i == safeBarSecond)
//                continue;
//
//            canvas.drawRect(i * barWidth, 0, barWidth * (i + 1), yPosition, p);
//        }

        collisionDetected = checkForCollision();
//        if (collisionDetected) {
//            //if there is one lets draw a red X
//            p.setColor(Color.RED);
//            p.setAlpha(255);
//            p.setStrokeWidth(5);
//            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
//                    lastCollision.x + 5, lastCollision.y + 5, p);
//            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
//                    lastCollision.x - 5, lastCollision.y + 5, p);
//        }
    }

    public void reset() {
        spiderArray = new Spider[NUM_OF_STARS];
        bee = new Bee(barWidth / 5);
        collisionDetected = false;
    }
}

