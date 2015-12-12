package org.wildstang.wildrank.androidv2.views.whiteboard;

import android.graphics.Bitmap;
import android.graphics.Canvas;

//this is a object for the magnets that can be moved around
public class Magnet {
    int x, y;
    Bitmap image;

    public Magnet(int x, int y, Bitmap image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public boolean checkClick(int x, int y) {
        return (x > this.x && x < this.x + image.getWidth() && y > this.y && y < this.y + image.getHeight());
    }

    //updates the position of the magnet
    public void update(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //draws the image on the canvas at its position
    public void draw(Canvas c) {
        c.drawBitmap(image, x, y, null);
    }
}
