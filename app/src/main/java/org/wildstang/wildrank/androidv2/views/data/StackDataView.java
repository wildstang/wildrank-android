package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.models.StackModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StackDataView extends View {
    List<List<StackModel>> stacks = new ArrayList<>();
    int stackCount = 0;

    Paint textPaint, existingTotesPaint, newTotesPaint, binPaint, noodlePaint, outlinePaint, droppedPaint, notScoredPaint;

    public StackDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        existingTotesPaint = new Paint();
        existingTotesPaint.setColor(Color.LTGRAY);
        newTotesPaint = new Paint();
        newTotesPaint.setColor(Color.DKGRAY);
        binPaint = new Paint();
        binPaint.setColor(Color.argb(255, 85, 107, 47)); // Dark Green
        noodlePaint = new Paint();
        noodlePaint.setColor(Color.YELLOW);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        droppedPaint = new Paint();
        droppedPaint.setColor(Color.argb(200, 255, 0, 0)); // Translucent red
        notScoredPaint = new Paint();
        notScoredPaint.setColor(Color.argb(200, 0, 0, 255)); // Translucent blue
    }

    public void acceptNewTeamData(List<Document> matchDocs) {
        if (matchDocs == null || matchDocs.isEmpty()) {
            stacks = new ArrayList<>();
            return;
        }
        stacks = new ArrayList<>();
        // Sorts the matches by match number
        Collections.sort(matchDocs, new MatchDocumentComparator());
        stackCount = 0;
        // Default, empty StackModel to compare to
        for (Document doc : matchDocs) {
            Map<String, Object> data = (Map<String, Object>) doc.getProperty("data");
            List<Map<String, Object>> stackData = (List<Map<String, Object>>) data.get("stacks");
            List<StackModel> matchStacks = new ArrayList<>();
            if (stackData.size() == 0) {
                // For a team that scored no stacks during a match, we should display a blank space.
                // Increment the stack count to account for this blank space
                // Add a "blank stack" record to the list
                matchStacks.add(new BlankStack());
                stackCount++;
                Log.d("wildrank", "blank stack for match " + (String) doc.getProperty("match_key"));
            } else {
                for (int j = 0; j < stackData.size(); j++) {
                    Log.d("wildrank", "stack for match " + (String) doc.getProperty("match_key") + ": " + stackData.get(j));
                    StackModel stack = StackModel.fromMap(stackData.get(j));
                    if (stack.isMeaningfulStack()) {
                        matchStacks.add(stack);
                        stackCount++;
                    } else {
                        Log.d("wildrank", "meaningless stack!");
                        continue;
                    }
                }
            }
            stacks.add(matchStacks);
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas c) {
        if (stacks.isEmpty()) {
            c.drawText("No data exists for this team.", 100, 100, textPaint);
        }
        // First, compute the dimensions of our drawn items so that they're scaled properly
        float toteWidthToHeightRatio = 0.7f;
        // Default tote width is calculated so that the stacks will completely fill the screen horizontally
        float toteWidth = (float) getWidth() / (float) stackCount;
        // Default height is calculated based on the width
        float toteHeight = toteWidth * toteWidthToHeightRatio;
        // If this would result in a complete stack (7 elements) overflowing the screen, we'll calculate the stack height
        // so that a complete stack would completely fill the screen vertically
        if (toteHeight * 7 > getHeight()) {
            toteHeight = (float) getHeight() / 7f; // gives equal vertical space to 6 totes and 1 bin
            toteWidth = toteHeight / toteWidthToHeightRatio;
        }


        Log.d("wildrank", "stack count: " + stackCount);
        Log.d("wildrank", "match count: " + stacks.size());

        int stackCount = 0;
        for (List<StackModel> stackModels : stacks) {
            for (StackModel stack : stackModels) {
                if (stack instanceof BlankStack) {
                    // Don't draw anything here.
                    stackCount++;
                    continue;
                }
                int totalStackHeight = 0;
                int preexistingHeight;
                if (stack.isPreexisting) {
                    preexistingHeight = stack.preexistingToteCount;
                } else {
                    preexistingHeight = 0;
                }
                float x = toteWidth * stackCount;

                for (int j = 0; j < preexistingHeight; j++) {
                    float left = x;
                    float right = x + toteWidth;
                    float bottom = getHeight() - (toteHeight * j);
                    float top = bottom - toteHeight;
                    c.drawRect(left, top, right, bottom, existingTotesPaint);
                    c.drawRect(left, top, right, bottom, outlinePaint);
                    totalStackHeight++;
                }

                for (int j = preexistingHeight; j < preexistingHeight + stack.toteCount; j++) {
                    float left = x;
                    float right = x + toteWidth;
                    float bottom = getHeight() - (toteHeight * j);
                    float top = bottom - toteHeight;
                    c.drawRect(left, top, right, bottom, newTotesPaint);
                    c.drawRect(left, top, right, bottom, outlinePaint);
                    totalStackHeight++;
                }

                if (stack.hasBin) {
                    float radius;
                    if (toteHeight > toteWidth) {
                        radius = (toteWidth / 2f);
                    } else {
                        radius = (toteHeight / 2f);
                    }
                    float cx = x + (toteWidth / 2);
                    float cy = getHeight() - (toteHeight * (preexistingHeight + stack.toteCount) + radius);
                    if (stack.binDropped) {
                        c.drawCircle(cx, cy, radius, droppedPaint);
                    } else {
                        c.drawCircle(cx, cy, radius, binPaint);
                    }
                    c.drawCircle(cx, cy, radius, outlinePaint);
                    totalStackHeight++;

                    if (stack.hasNoodle) {
                        float noodleRadius = radius / 3;
                        c.drawCircle(cx, cy, noodleRadius, noodlePaint);
                        c.drawCircle(cx, cy, noodleRadius, outlinePaint);
                    }
                }

                if (stack.stackDropped) {
                    float left = x;
                    float right = x + toteWidth;
                    float bottom = getHeight();
                    float top = bottom - (toteHeight * totalStackHeight);
                    c.drawRect(left, top, right, bottom, droppedPaint);
                } else if (stack.notScored) {
                    float left = x;
                    float right = x + toteWidth;
                    float bottom = getHeight();
                    float top = bottom - (toteHeight * totalStackHeight);
                    c.drawRect(left, top, right, bottom, notScoredPaint);
                }

                stackCount++;
            }
            // Draw line to separate matches
            c.drawLine(stackCount * toteWidth, 0, stackCount * toteWidth, getHeight(), outlinePaint);
        }
    }

    class MatchDocumentComparator implements Comparator<Document> {

        @Override
        public int compare(Document lhs, Document rhs) {
            try {
                int lhsMatchNumber = Integer.parseInt(((String) lhs.getProperty("match_key")).replaceAll("[0-9]+[a-zA-Z]+_[a-zA-Z]+", ""));
                int rhsMatchNumber = Integer.parseInt(((String) rhs.getProperty("match_key")).replaceAll("[0-9]+[a-zA-Z]+_[a-zA-Z]+", ""));
                Log.d("wildrank", "lhs: " + lhsMatchNumber + ", rhs: " + rhsMatchNumber);
                return (lhsMatchNumber - rhsMatchNumber);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    private class BlankStack extends StackModel {
        // Empty class to represent a blank space
    }
}
