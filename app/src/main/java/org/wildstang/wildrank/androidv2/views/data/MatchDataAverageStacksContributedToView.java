package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;

import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 3/18/2015.
 */
public class MatchDataAverageStacksContributedToView extends MatchDataView implements IMatchDataView {

    public MatchDataAverageStacksContributedToView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        double totalStacks = 0;
        double totalMatches = 0;
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }
        for (Document document : documents) {
            totalMatches++;
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            List<Map<String, Object>> stacks = (List<Map<String, Object>>) data.get("stacks");
            totalStacks += stacks.size();
        }
        double average;
        if(totalMatches == 0) {
            average = 0;
        } else {
            average = totalStacks / totalMatches;
        }
        setValueText(formatNumberAsString(average));
    }
}
