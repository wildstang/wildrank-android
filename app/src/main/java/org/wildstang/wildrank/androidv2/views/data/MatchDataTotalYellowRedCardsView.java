package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;

import java.util.List;
import java.util.Map;

public class MatchDataTotalYellowRedCardsView extends MatchDataView implements IMatchDataView {

    public MatchDataTotalYellowRedCardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        int totalYellowCards = 0;
        int totalRedCards = 0;
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }
        for (Document document : documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            int yellowCards = (boolean) data.get("post_match-yellow_card") == true ? 1 : 0;
            int redCards = (boolean) data.get("post_match-red_card") == true ? 1 : 0;
            totalYellowCards += yellowCards;
            totalRedCards += redCards;
        }
        setValueText(totalYellowCards + "/" + totalRedCards);
    }
}
