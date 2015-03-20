package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import org.wildstang.wildrank.androidv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liam on 3/20/2015.
 */
public class StackView extends View
{
    Bitmap tote;
    Bitmap bin;
    Bitmap noodle;
    List<Stack> stacks = new ArrayList<>();

    public StackView(Context context)
    {
        super(context);
        bin = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.greenbin);
        tote = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.graytote);
        noodle = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noodle);
        double scale = getHeight() / 10;
        tote = Bitmap.createScaledBitmap(tote, (int)(scale * (tote.getWidth()/tote.getHeight())), (int)(scale), false);
        scale = scale * (bin.getHeight() / tote.getHeight());
        bin = Bitmap.createScaledBitmap(bin, (int)(scale * (bin.getWidth()/bin.getHeight())), (int)(scale), false);
        scale = scale * (noodle.getHeight() / noodle.getHeight());
        noodle = Bitmap.createScaledBitmap(noodle, (int)(scale * (noodle.getWidth()/noodle.getHeight())), (int)(scale), false);
    }

    @Override
    public void onDraw(Canvas c)
    {
        for(int i = 0; i < stacks.size(); i++)
        {
            stacks.get(i).draw(c);
        }
    }

    public class Stack
    {
        int totes, stackNum;
        boolean hasBin, hasNoodle;

        public Stack(int totes, boolean hasBin, boolean hasNoodle, int stackNum)
        {
            this.totes = totes;
            this.hasBin = hasBin;
            this.hasNoodle = hasNoodle;
            this.stackNum = stackNum;
        }

        public void draw(Canvas c)
        {
            for(int i = 0; i < totes; i++)
            {
                c.drawBitmap(tote, 10 + stackNum * tote.getWidth(), getHeight() - i * tote.getHeight(), null);
            }
            if(hasNoodle)
            {
                c.drawBitmap(noodle, 10 + stackNum * tote.getWidth(), getHeight() - totes * getHeight(), null);
            }
            if(hasBin)
            {
                c.drawBitmap(bin, 10 + stackNum * tote.getWidth(), getHeight() - totes * getHeight(), null);
            }
        }
    }
}
