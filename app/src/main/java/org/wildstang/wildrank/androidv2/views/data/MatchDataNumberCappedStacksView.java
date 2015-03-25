package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;
import org.wildstang.wildrank.androidv2.models.StackModel;
import org.wildstang.wildrank.androidv2.views.scouting.ScoutingStacksView;

import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 3/18/2015.
 */
public class MatchDataNumberCappedStacksView extends MatchDataView implements IMatchDataView {

    public MatchDataNumberCappedStacksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }
        int cappedStacks = 0;
        for (Document document : documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            if (data == null) {
                return;
            }
            List<Map<String, Object>> stacks = (List<Map<String, Object>>) data.get("stacks");
            for (Map<String, Object> stack : stacks) {
                boolean includesBin = (boolean) stack.get(StackModel.HAS_BIN_KEY);
                boolean binDropped = (boolean) stack.get(StackModel.BIN_DROPPED_KEY);
                if (includesBin && !binDropped) {
                    cappedStacks++;
                }
            }
        }
        setValueText("" + cappedStacks);
    }
}
