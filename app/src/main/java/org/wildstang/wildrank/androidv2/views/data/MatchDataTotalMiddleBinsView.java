package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.MathObservable;

public class MatchDataTotalMiddleBinsView extends MatchDataView implements IMatchDataView {

    public MatchDataTotalMiddleBinsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }

        Observable foulsObservable = Observable.from(documents)
                .map(doc -> (Map<String, Object>) doc.getProperty("data"))
                .map(data -> Integer.parseInt((String) data.get("auto-center_bins")));

        MathObservable.sumInteger(foulsObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sum -> setValueText("" + sum), error -> Log.d("wildrank", this.getClass().getName()));

    }
}

