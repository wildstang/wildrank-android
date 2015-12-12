package org.wildstang.wildrank.androidv2.views.whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import org.wildstang.wildrank.androidv2.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a custom view that allows the user to plan strategies by drawing on an image of the field
 * and moving around game elements and robots, which are referred to internally as magnets.
 * <p>
 * This view provides abstractions for magnets and handles most default behavior, but this will still
 * have to be manually updated each year. Specifically, you must add a new image of the field,
 * images for each magnet you want to be able to use on the field, and manually configure the position
 * and size of the field and buttons.
 * <p>
 * The current layout looks like this:
 * <p>
 * +----+-------------+----+
 * |    |             |    |
 * |    |             |    |
 * | L  |    Field    | R  |
 * |    |             |    |
 * |    |             |    |
 * +----+-------------+----+
 * <p>
 * Magnets are located in the left panel, and miscellaneous buttons are in the right panel. Three
 * constants (LEFT_PANEL_WEIGHT, FIELD_WEIGHT, RIGHT_PANEL_WEIGHT) determine how these panels stretch
 * relative to each other to consume the available space. The current implementation will resize and
 * position the field so that it is centered in the middle region and is as big as possible while still
 * maintaining aspect ratio.
 */
public class WhiteboardView extends View {

    Bitmap field;

    Paint canvasPaint, penPaint;

    boolean penActive = false;

    // Offset the position of the magnet and the position where the magnet was touched
    int xOffset, yOffset;

    int fieldX, fieldY;

    LinkedList<Magnet> magnets = new LinkedList<>();
    Magnet activeMagnet = null;
    List<List<Point>> penLines = new ArrayList<>();

    public WhiteboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        field = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.field);

        canvasPaint = new Paint();
        canvasPaint.setColor(Color.WHITE);

        penPaint = new Paint();
        penPaint.setColor(Color.BLUE);

        setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    DraggableImage info = (DraggableImage) event.getLocalState();
                    Bitmap bitmap = info.getBitmap(getContext());
                    magnets.add(new Magnet((int) (event.getX() - bitmap.getWidth() / 2), (int) (event.getY() - bitmap.getHeight() / 2), info.getBitmap(getContext())));
                    invalidate();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    // Initializes images and buttons
    public void init() {
        // Compute the appropriate size of the field so that it is as big as possible while still
        // retaining aspect ratio. This math isn't totally arbitrary!
        int width, height;
        double aspectRatio = (double) field.getWidth() / (double) field.getHeight();
        if (((double) getWidth()) / aspectRatio > getHeight()) {
            height = getHeight();
            width = (int) (aspectRatio * height);
            fieldX = (getWidth() - width) / 2;
            fieldY = 0;
        } else {
            width = getWidth();
            height = (int) (((double) width) / aspectRatio);
            fieldY = (getHeight() - height) / 2;
            fieldX = 0;
        }

        field = Bitmap.createScaledBitmap(field, width, height, false);

        this.setOnTouchListener((v, event) -> {
            invalidate();

            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    activeMagnet = null;

                    // Iterate in reverse order; newest magnets will be at the end of the list and
                    // therefore have a higher z-ordering, so we check clicks against them first
                    Iterator<Magnet> it = magnets.descendingIterator();
                    while (it.hasNext()) {
                        Magnet magnet = it.next();
                        if (magnet.checkClick(x, y)) {
                            activeMagnet = magnet;
                            xOffset = x - magnet.x;
                            yOffset = y - magnet.y;
                            return true;
                        }
                    }

                    // A magnet didn't consume the click, so draw with the pen
                    penActive = true;
                    List<Point> line = new ArrayList<>();
                    line.add(new Point(x, y));
                    penLines.add(line);

                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (activeMagnet != null) {
                        activeMagnet.update(x - xOffset, y - yOffset);
                    }

                    if (penActive && penLines.size() > 0) {
                        penLines.get(penLines.size() - 1).add(new Point(x, y));
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    penActive = false;
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onDraw(Canvas canvas) {

        // Clear the canvas
        canvas.drawRect(0, 0, getWidth(), getHeight(), canvasPaint);

        canvas.drawBitmap(field, fieldX, fieldY, null);

        for (List<Point> points : penLines) {
            for (int j = 1; j < points.size(); j++) {
                Point last = points.get(j - 1);
                Point point = points.get(j);
                canvas.drawLine(last.x, last.y, point.x, point.y, penPaint);
            }
        }

        for (Magnet magnet : magnets) {
            magnet.draw(canvas);
        }
    }
}