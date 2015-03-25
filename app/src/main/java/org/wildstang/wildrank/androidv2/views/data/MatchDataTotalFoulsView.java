package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;

import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 3/18/2015.
 */
public class MatchDataTotalFoulsView extends MatchDataView implements IMatchDataView {

    public MatchDataTotalFoulsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        int totalFouls = 0;
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }
        for (Document document : documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            int fouls = (int) data.get("post_match-foul");
            totalFouls += fouls;
        }
        setValueText("" + totalFouls);
    }
}
