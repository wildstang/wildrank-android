package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;
import org.wildstang.wildrank.androidv2.views.scouting.ScoutingStacksView;

import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 3/18/2015.
 */
public class MatchDataMaxHeightView extends MatchDataView implements IMatchDataView {

    public MatchDataMaxHeightView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }
        int maxHeight = 0;
        for (Document document : documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            List<Map<String, Object>> stacks = (List<Map<String, Object>>) data.get("stacks");
            for (Map<String, Object> stack : stacks) {
                boolean preexisting = (boolean) stack.get(ScoutingStacksView.PREEXISTING_KEY);
                int preexistingHeight = (int) stack.get(ScoutingStacksView.PREEXISTING_HEIGHT_KEY);
                int toteCount = (int) stack.get(ScoutingStacksView.TOTE_COUNT_KEY);
                int height;
                if (preexisting) {
                    height = preexistingHeight + toteCount;
                } else {
                    height = toteCount;
                }
                maxHeight = Math.max(maxHeight, height);
                Log.d("wildstang", stack.toString());
            }
        }
        setValueText("" + maxHeight);
    }
}
