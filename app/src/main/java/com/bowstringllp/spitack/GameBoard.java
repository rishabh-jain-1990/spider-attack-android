package com.bowstringllp.spitack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by rishabhjain on 10/31/15.
 */
public class GameBoard extends View {

    private final Paint p;
    private final int NUM_OF_STARS;
    private final NinePatchDrawable spiderImage;
    private final Bitmap playerImage;
    private final int spiderImageWidth;
    private final int spiderImageHeight;
    private final int playerImageWidth;
    private final int playerImageHeight;
    private final Bitmap spiderImageBitmap;
    private int barWidth;
    private int yPosition = 5;
    private int addFactor = 20;
    private int safeBarFirst = 0;
    private int safeBarSecond = 1;
    private Bar[] barArray;
    private boolean collisionDetected = false;
    private Point lastCollision = new Point(-1, -1);
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        //it's best not to create any new objects in the on draw
        //initialize them as class variables here
        p = new Paint();
        NUM_OF_STARS = MainActivity.getNoOfStars();
        barArray = new Bar[NUM_OF_STARS];
        spiderImage = (NinePatchDrawable) getResources().getDrawable(R.drawable.spider_patch);
        spiderImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spider_patch);
        playerImage = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        player = new Player();

        spiderImageWidth = spiderImage.getMinimumWidth();
        spiderImageHeight = spiderImage.getMinimumHeight();
        playerImageWidth = playerImage.getWidth();
        playerImageHeight = playerImage.getHeight();
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

        Rect r1 = new Rect(player.getxStart(), player.getyStart(), player.getxEnd(), player.getyEnd());
        Rect r3 = new Rect(r1);

        for (int h = 0; h < NUM_OF_STARS; h++) {
            if (player.getxStart() > barArray[h].getxEnd())
                continue;
            else if (player.getxEnd() < barArray[h].getxStart())
                continue;

            Rect r2 = new Rect(barArray[h].getxStart(), 0, barArray[h].getxEnd(), barArray[h].getyEnd());
            if (r1.intersect(r2)) {
                for (float i = r1.left; i < r1.right; i++) {
                    for (float j = r1.top; j < r1.bottom; j++) {
                        try {
                            int scaledSpiderX = (int) ((i - r2.left) / r2.width() * spiderImageWidth);
                            int scaledSpiderY = (int) ((j - r2.top) / r2.height() * spiderImageHeight);
                            int scaledPlayerX = (int) ((i - r3.left) / r3.width() * playerImageWidth);
                            int scaledPlayerY = (int) ((j - r3.top) / r3.height() * playerImageHeight);

                            if (playerImage.getPixel(scaledPlayerX, scaledPlayerY) !=
                                    Color.TRANSPARENT) {
                                if (spiderImageBitmap.getPixel(scaledSpiderX, scaledSpiderY) !=
                                        Color.TRANSPARENT) {
                                    lastCollision = new Point(barArray[h].getxStart() +
                                            (int) i - r2.left, (int) (0 + j - r2.top));
                                    return true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        lastCollision = new Point(-1, -1);
        return false;
    }

    public boolean isCollisionDetected() {
        return collisionDetected;
    }

    public void setCollisionDetected(boolean collisionDetected) {
        this.collisionDetected = collisionDetected;
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {

        if (player.getyEnd() == 0) {
            player.setyEnd(getHeight());
            player.setyStart(getHeight() - player.getHeight());
            player.setxStart(getWidth() / 2);
        }
        //create a black canvas
        p.setColor(Color.BLACK);
        p.setAlpha(0);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        p.setAlpha(255);
        if (barWidth == 0)
            barWidth = getWidth() / NUM_OF_STARS;

        p.setColor(Color.CYAN);
        //draw the stars
        p.setStrokeWidth(barWidth);

        for (int i = 0; i < NUM_OF_STARS; i++) {
            if (barArray[i] == null)
                barArray[i] = new Bar();

            barArray[i].setxStart(barWidth * i);
            barArray[i].setWidth(barWidth);
            barArray[i].setxEnd(barArray[i].getxStart() + barWidth);

            if (barArray[i].getCountdown() > 0) {
                continue;
            }

            if (barArray[i].getyEnd() >= getHeight()) {
                barArray[i].setAddFactor(barArray[i].getAddFactor() * -1);
                barArray[i].setyTurning(new Random().nextInt(getHeight() / 2));
            } else if (barArray[i].getyEnd() <= barArray[i].getyTurning()) {
                barArray[i].setAddFactor(barArray[i].getAddFactor() * -1);
//                safeBarFirst = new Random().nextInt(NUM_OF_STARS);
//                do {
//                    safeBarSecond = new Random().nextInt(NUM_OF_STARS);
//                } while (safeBarFirst == safeBarSecond);
            }

            barArray[i].setyEnd(barArray[i].getyEnd() + barArray[i].getAddFactor());

            Rect rect = new Rect(barArray[i].getxStart(), 0, barArray[i].getxEnd(), barArray[i].getyEnd());
            spiderImage.setBounds(rect);
            spiderImage.draw(canvas);
            //canvas.drawBitmap(spiderImage, null, rect, p);

            p.setColor(Color.GREEN);

        }

        if ((player.getxEnd() + player.getAddFactor()) <= getWidth() && (player.getxEnd() + player.getAddFactor()) >= 0) {
            player.setxStart(player.getxStart() + player.getAddFactor());
            player.setxEnd(player.getxStart() + player.getWidth());
        }
        p.setColor(Color.MAGENTA);
        //draw the stars
        p.setStrokeWidth(player.getWidth());

        Rect rect = new Rect(player.getxStart(), player.getyStart(), player.getxEnd(), player.getyEnd());
        canvas.drawBitmap(playerImage, null, rect, p);
//        for (int i = 0; i < NUM_OF_STARS; i++) {
//            if (i == safeBarFirst || i == safeBarSecond)
//                continue;
//
//            canvas.drawRect(i * barWidth, 0, barWidth * (i + 1), yPosition, p);
//        }

        collisionDetected = checkForCollision();
        if (collisionDetected) {
            //if there is one lets draw a red X
            p.setColor(Color.RED);
            p.setAlpha(255);
            p.setStrokeWidth(5);
            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
                    lastCollision.x + 5, lastCollision.y + 5, p);
            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
                    lastCollision.x - 5, lastCollision.y + 5, p);
        }
    }

    public void reset() {
        barArray = new Bar[NUM_OF_STARS];
        player = new Player();
        collisionDetected = false;
    }
}

