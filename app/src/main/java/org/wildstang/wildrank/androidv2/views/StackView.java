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

    public StackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bin = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.binside);
        tote = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.toteside);
        noodle = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noodle);
        double scale = getHeight() / 10;
        System.out.println("height: " + getHeight() + ", scale: " + scale);
        double ogscale = scale;
        tote = Bitmap.createScaledBitmap(tote, (int) (scale * (tote.getWidth() / tote.getHeight())), (int) (scale), false);
        scale = ogscale * (bin.getHeight() / tote.getHeight());
        bin = Bitmap.createScaledBitmap(bin, (int) (scale * (bin.getWidth() / bin.getHeight())), (int) (scale), false);
        scale = ogscale * (noodle.getHeight() / noodle.getHeight());
        noodle = Bitmap.createScaledBitmap(noodle, (int) (scale * (noodle.getWidth() / noodle.getHeight())), (int) (scale), false);
    }

    public void updateData(String teamKey) {
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
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDraw(Canvas c) {
        try {

            for (int i = 0; i < stacks.size(); i++) {
                stacks.get(i).draw(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                c.drawBitmap(tote, stackNum * (tote.getWidth() + 10), getHeight() - (i * tote.getHeight()), null);
            }
            if (hasNoodle) {
                c.drawBitmap(noodle, stackNum * (tote.getWidth() + 10), getHeight() - (totes * getHeight()), null);
            }
            if (hasBin) {
                c.drawBitmap(bin, stackNum * (tote.getWidth() + 10), getHeight() - (totes * getHeight()), null);
            }
        }
    }
}
