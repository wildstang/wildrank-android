package org.wildstang.wildrank.androidv2.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import org.wildstang.wildrank.androidv2.R;

/**
 * Created by Liam on 1/24/2015.
 */
public class WhiteboardView extends View
{
    Bitmap field;
    Bitmap bin;
    Bitmap tote;
    Bitmap coop;
    Bitmap litter;
    Bitmap robot;

    public WhiteboardView(Context context)
    {
        super(context);
        field = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.field);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(0, 0, getWidth()/3, getHeight(), paint);
        canvas.drawBitmap(field, new Rect(0, 0, field.getWidth(), field.getHeight()), new Rect(getWidth()/3, 0, getWidth(), getHeight()), null);
    }
}
