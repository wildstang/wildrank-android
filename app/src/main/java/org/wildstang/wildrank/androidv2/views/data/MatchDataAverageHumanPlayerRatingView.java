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
public class MatchDataAverageHumanPlayerRatingView extends MatchDataView implements IMatchDataView {

    public MatchDataAverageHumanPlayerRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }
        double numMatches = 0;
        double ratingSum = 0;
        for (Document document : documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            if (data == null) {
                continue;
            }

            Log.d("wildrank", data.toString());

            if (data.get("post_match-hp_rating") == null) {
                continue;
            }
            String hpRatingString = (String) data.get("post_match-hp_rating");
            int hpRating = Integer.parseInt(hpRatingString);
            if (hpRating == 0) {
                continue;
            }
            ratingSum += hpRating;
            numMatches++;
        }
        double average = ratingSum / numMatches;
        setValueText(formatNumberAsString(average));
    }
}
