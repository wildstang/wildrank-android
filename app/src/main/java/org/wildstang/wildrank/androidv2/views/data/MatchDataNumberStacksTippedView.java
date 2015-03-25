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
public class MatchDataNumberStacksTippedView extends MatchDataView implements IMatchDataView {

    public MatchDataNumberStacksTippedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }
        int tipped = 0;
        for (Document document : documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            if (data == null) {
                return;
            }

            Log.d("wildrank", data.toString());

            if (data.get("stacks_tipped_over") == null) {
                continue;
            }
            int stacksTippedInMatch = (int) data.get("stacks_tipped_over");
            tipped += stacksTippedInMatch;
        }
        setValueText("" + tipped);
    }
}
