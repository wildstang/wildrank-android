package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * A subclass of ImageView that applies a linear gradient to the bottom half of the image. This is
 * mostly useful for cases where text needs to be overlaid on an image.
 */
public class LinearGradientImageView extends ImageView {
    public LinearGradientImageView(Context context) {
        super(context);
    }

    public LinearGradientImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearGradientImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap source = ((BitmapDrawable) drawable).getBitmap();
        Bitmap outBitmap = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());

        // Create shaders
        Shader bitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Shader linearGradient = new LinearGradient(0, source.getHeight() / 2, 0, source.getHeight(), Color.TRANSPARENT, 0xB4000000, Shader.TileMode.CLAMP);

        // create a shader that combines both effects
        ComposeShader shader = new ComposeShader(bitmapShader, linearGradient, PorterDuff.Mode.DST_OUT);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setShader(shader);

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Canvas c = new Canvas(outBitmap);
        c.drawRect(0, 0, source.getWidth(), source.getHeight(), black);
        c.drawPaint(p);
        source.recycle();
        canvas.drawBitmap(outBitmap, 0, 0, null);
    }
}
