package org.wildstang.wildrank.androidv2.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.wildstang.wildrank.androidv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liam on 1/24/2015.
 */
public class WhiteboardView extends View
{
    Bitmap field;
    Bitmap binMagnet;
    Bitmap toteMagnet;
    Bitmap coopMagnet;
    Bitmap litterMagnet;
    Bitmap robotMagnet;
    Bitmap bin;
    Bitmap tote;
    Bitmap coop;
    Bitmap litter;
    Bitmap robot;
    boolean run = false;
    double scale;
    int ogheight;
    List<Magnet> magnets = new ArrayList<>();
    List<Button> buttons = new ArrayList<>();
    List<List<Point>> points = new ArrayList<>();
    boolean magnetheld = false;
    int currentmagnet = 0;
    boolean penOn = false;

    public WhiteboardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        field = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.field);
        bin = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.greenbin);
        tote = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.graytote);
        coop = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.yellowtote);
        litter = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noodle);
        //robot = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.field);
    }

    public void init()
    {
        scale = ((double)getHeight()/(double)field.getHeight());
        field = Bitmap.createScaledBitmap(field,(int) (scale * field.getWidth()), getHeight(), false);
        binMagnet = Bitmap.createScaledBitmap(bin,(int)(scale * bin.getWidth()), (int)(scale * bin.getHeight()), false);
        toteMagnet = Bitmap.createScaledBitmap(tote, (int)(scale * tote.getWidth()), (int)(scale * tote.getHeight()), false);
        coopMagnet = Bitmap.createScaledBitmap(coop,  (int)(scale * coop.getWidth()), (int)(scale * coop.getHeight()), false);
        litterMagnet = Bitmap.createScaledBitmap(litter, (int) (scale * litter.getWidth()), (int)(scale * litter.getHeight()), false);
        scale = (((double)getHeight()/8.0)/(double)tote.getWidth());
        bin = Bitmap.createScaledBitmap(bin,(int)(scale * bin.getWidth()), (int)(scale * bin.getHeight()), false);
        tote = Bitmap.createScaledBitmap(tote, (int)(scale * tote.getWidth()), (int)(scale * tote.getHeight()), false);
        coop = Bitmap.createScaledBitmap(coop,  (int)(scale * coop.getWidth()), (int)(scale * coop.getHeight()), false);
        litter = Bitmap.createScaledBitmap(litter, (int) (scale * litter.getWidth()), (int)(scale * litter.getHeight()), false);
        //robot = Bitmap.createScaledBitmap(robot, 120, 120, false);
        //robotMagnet = Bitmap.createScaledBitmap(robot, 120, 120, false);
        int width = ((getWidth() / 6) + field.getWidth());
        int buttonWidth = getWidth() - (width + 20);
        buttons.add(new Button(width + 10, 10, buttonWidth, 2*buttonWidth/3 + 10, "Clear!", false));
        buttons.add(new Button(width + 10, 2*buttonWidth/3 + 30, buttonWidth, 2*buttonWidth/3 + 10, "Pen On/Off", true));
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                invalidate();
                int x = (int) event.getX();
                int y = (int) event.getY();
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (!penOn && y >= 10 && y <= 10 + tote.getHeight() && x >= 10 && x <= tote.getWidth() + 10)
                        {
                            currentmagnet = magnets.size();
                            magnets.add(new Magnet(x, y, toteMagnet));
                            magnetheld = true;
                        }
                        else if (!penOn && y >= 20 + tote.getHeight() && y <= 20 + tote.getHeight() + bin.getHeight() && x >= 10 && x <= bin.getWidth() + 10)
                        {
                            currentmagnet = magnets.size();
                            magnets.add(new Magnet(x, y, binMagnet));
                            magnetheld = true;
                        }
                        else if (!penOn && y >= 30 + tote.getHeight() + bin.getHeight() && y <= 30 + tote.getHeight() + bin.getHeight() + coop.getHeight() && x >= 10 && x <= coop.getWidth() + 10)
                        {
                             currentmagnet = magnets.size();
                             magnets.add(new Magnet(x, y, coopMagnet));
                             magnetheld = true;
                        }
                        else if (!penOn && y >= 40 + tote.getHeight() + bin.getHeight() + coop.getHeight() && y <= 40 + tote.getHeight() + bin.getHeight() + coop.getHeight() + litter.getHeight() && x >= 10 && x <= litter.getWidth() + 10)
                        {
                            currentmagnet = magnets.size();
                            magnets.add(new Magnet(x, y, litterMagnet));
                            magnetheld = true;
                        }
                        else
                        {
                            if(penOn)
                            {
                                points.add(new ArrayList<Point>());
                                points.get(points.size() - 1).add(new Point(x, y));
                            }
                            magnetheld = false;
                            for(int i = 0; i < magnets.size(); i++)
                            {
                                Magnet magnet = magnets.get(i);
                                if(x >= magnet.x && x <= magnet.x + magnet.img.getWidth() && x >= magnet.y && y <= magnet.y + magnet.img.getHeight())
                                {
                                    currentmagnet = i;
                                    magnetheld = true;
                                }
                            }
                            for(int i = 0; i < buttons.size(); i++)
                            {
                                Button button = buttons.get(i);
                                if(x >= button.x && x <= button.x + button.width && y >= button.y && y <= button.y + button.height)
                                {
                                    if(button.toggle)
                                    {
                                        buttons.get(i).pushed = !buttons.get(i).pushed;
                                        if(button.name.equals("Pen On/Off"))
                                        {
                                            penOn = buttons.get(i).pushed;
                                        }
                                    }
                                    else
                                    {
                                        buttons.get(i).pushed = true;
                                    }
                                }
                                else if(!button.toggle)
                                {
                                    buttons.get(i).pushed = false;
                                }
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if(magnetheld)
                        {
                            magnets.get(currentmagnet).update(x, y);
                        }
                        if(penOn && points.size() > 0)
                        {
                            points.get(points.size() - 1).add(new Point(x, y));
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        for(int i = 0; i < buttons.size(); i++)
                        {
                            Button button = buttons.get(i);
                            if(x >= button.x && x <= button.x + button.width && y >= button.y && y <= button.y + button.height && button.pushed && !button.toggle)
                            {
                                if(button.name.equals("Clear!"))
                                {
                                    magnets = new ArrayList<>();
                                    points = new ArrayList<>();
                                }
                            }
                            if(!button.toggle)
                            {
                                buttons.get(i).pushed = false;
                            }
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        if(!run)
        {
            init();
            run = true;
        }
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, getWidth() / 6, getHeight(), paint);
        canvas.drawBitmap(field, getWidth() / 6, 0, null);
        canvas.drawBitmap(tote, 10, 10, null);
        canvas.drawBitmap(bin, 10, 20 + tote.getHeight(), null);
        canvas.drawBitmap(coop, 10, 40 + tote.getHeight() + bin.getHeight(), null);
        canvas.drawBitmap(litter, 10, 50 + tote.getHeight() + bin.getHeight() + coop.getHeight(), null);
        for(int i = 0; i < buttons.size(); i++)
        {
            buttons.get(i).draw(canvas);
        }
        for(int i = 0; i < magnets.size(); i++)
        {
            magnets.get(i).draw(canvas);
        }
        paint.setColor(Color.RED);
        for(int i = 0; i < points.size(); i++)
        {
            for(int j = 1; j < points.get(i).size(); j++)
            {
                if(j > 0)
                {
                    Point last = points.get(i).get(j-1);
                    Point point = points.get(i).get(j);
                    canvas.drawLine(last.x, last.y, point.x, point.y, paint);
                }
            }
        }
    }

    public class Magnet
    {
        int x, y;
        Bitmap img;

        public Magnet(int x, int y, Bitmap img)
        {
            this.x = x;
            this.y = y;
            this.img = img;
        }

        public void update(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public void draw(Canvas c)
        {
            c.drawBitmap(img, x, y, null);
        }
    }

    public class Button
    {
        int x, y, width, height;
        String name;
        boolean pushed;
        boolean toggle;

        public Button(int x, int y, int width, int height, String name, boolean toggle)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.name = name;
            this.toggle = toggle;
            pushed = false;
        }

        public void draw(Canvas c)
        {
            Paint paint = new Paint();
            if(pushed)
            {
                paint.setColor(Color.DKGRAY);
            }
            else
            {
                paint.setColor(Color.LTGRAY);
            }
            c.drawRect(x, y, x + width, y + height, paint);
            paint.setColor(Color.BLACK);
            c.drawText(name, x + width/3, y + height/3, paint);
        }
    }

    public class Point
    {
        int x,y;

        public Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}