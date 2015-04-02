package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.MathObservable;

/**
 * Created by Nathan on 3/18/2015.
 */
public class MatchDataTotalStacksContributedToView extends MatchDataView implements IMatchDataView {

    public MatchDataTotalStacksContributedToView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        int totalStacks = 0;
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }

        Observable stacksObservable = Observable.from(documents)
                .map(doc -> (Map<String, Object>) doc.getProperty("data"))
                .map(data -> (List<Map<String, Object>>) data.get("stacks"))
                .map(stacks -> stacks.size());

        MathObservable.sumInteger(stacksObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sum -> setValueText("" + sum));

        /*for (Document document : documents) {
            Map<String, Object> data = (Map<String, Object>) document.getProperty("data");
            List<Map<String, Object>> stacks = (List<Map<String, Object>>) data.get("stacks");
            for (Object stack : stacks) {
                totalStacks++;
            }
        }
        setValueText("" + totalStacks);*/
    }
}
