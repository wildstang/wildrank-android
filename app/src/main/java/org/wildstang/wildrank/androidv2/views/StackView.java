package org.wildstang.wildrank.androidv2.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Liam on 3/20/2015.
 */
public class StackView extends View {
    Bitmap tote;
    Bitmap bin;
    Bitmap noodle;
    List<Stack> stacks = new ArrayList<>();

    boolean inited = false;

    public StackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init()
    {
        bin = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.binside);
        tote = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.toteside);
        noodle = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noodle);
        double scale = getHeight() / 10;
        System.out.println("height: " + getHeight() + ", scale: " + scale);
        double ogscale = scale;
        tote = Bitmap.createScaledBitmap(tote, (int) (scale * (tote.getWidth() / tote.getHeight())), (int) (scale), false);
        scale = ogscale * (bin.getHeight() / tote.getHeight());
        bin = Bitmap.createScaledBitmap(bin, (int) (scale * ((double)bin.getWidth() /(double) bin.getHeight())), (int) (scale), false);
        scale = ogscale * (noodle.getHeight() / noodle.getHeight());
        noodle = Bitmap.createScaledBitmap(noodle, (int) (scale * ((double)noodle.getWidth() / (double)noodle.getHeight())), (int) (scale), false);
    }

    public void updateData(String teamKey) {
        stacks = new ArrayList<>();
        try {
            List<Document> matchResults = DatabaseManager.getInstance(getContext()).getMatchResultsForTeam(teamKey);
            for (Document doc : matchResults) {
                Map<String, Object> data = (Map<String, Object>) doc.getProperty("data");
                List<Map<String, Object>> stackData = (List<Map<String, Object>>) data.get("stacks");
                for (int j = 0; j < stackData.size(); j++) {
                    int totes = (int) stackData.get(j).get("tote_count");
                    boolean hasBin = (boolean) stackData.get(j).get("has_bin");
                    boolean hasNoodle = (boolean) stackData.get(j).get("has_noodle");
                    stacks.add(new Stack(totes, hasBin, hasNoodle, j));
                }
            }
            invalidate();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDraw(Canvas c) {
        if(!inited)
        {
            inited = true;
            init();
        }
        for (int i = 0; i < stacks.size(); i++) {
            stacks.get(i).draw(c);
        }
    }

    public class Stack {
        int totes, stackNum;
        boolean hasBin, hasNoodle;

        public Stack(int totes, boolean hasBin, boolean hasNoodle, int stackNum) {
            this.totes = totes;
            this.hasBin = hasBin;
            this.hasNoodle = hasNoodle;
            this.stackNum = stackNum;
        }

        public void draw(Canvas c) {
            for (int i = 0; i < totes; i++) {
                System.out.println("Drawing Tote X: " + (stackNum * (tote.getWidth() + 10)) + " Y: " + (getHeight() - (i * tote.getHeight())));
                c.drawBitmap(tote, stackNum * (tote.getWidth() + 10), getHeight() - (i * tote.getHeight()), null);
            }
            if (hasNoodle) {
                System.out.println("Drawing Noodle X: " + (stackNum * (tote.getWidth() + 10)) + " Y: " + (getHeight() - (totes * tote.getHeight())));
                c.drawBitmap(noodle, stackNum * (tote.getWidth() + 10), getHeight() - ((totes+1) * getHeight()), null);
            }
            if (hasBin) {
                System.out.println("Drawing Bin X: " + (stackNum * (tote.getWidth() + 10)) + " Y: " + (getHeight() - (totes * tote.getHeight())));
                c.drawBitmap(bin, stackNum * (tote.getWidth() + 10), getHeight() - (totes * getHeight()), null);
            }
        }
    }
}
