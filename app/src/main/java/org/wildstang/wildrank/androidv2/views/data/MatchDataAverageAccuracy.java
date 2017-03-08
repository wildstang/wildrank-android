package org.wildstang.wildrank.androidv2.views.data;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.interfaces.IMatchDataView;
import org.wildstang.wildrank.androidv2.models.StackModel;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.MathObservable;
import rx.schedulers.Schedulers;

public class MatchDataAverageAccuracy extends MatchDataView implements IMatchDataView {

    public MatchDataAverageAccuracy(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void calculateFromDocuments(List<Document> documents) {
        if (documents == null) {
            return;
        } else if (documents.size() == 0) {
            return;
        }

        Observable<Double> stacksObservable = Observable.from(documents)
                .map(doc -> (Map<String, Object>) doc.getProperty("data"))
                .filter(data -> data.get("high") != null)
                .map(data -> Double.parseDouble((String) data.get("high")))
                .filter(ranking -> ranking != -1.0)
                .defaultIfEmpty(0.0)
                .subscribeOn(Schedulers.computation());

        MathObservable.averageDouble(stacksObservable)
                .map(average -> formatNumberAsString(average))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(average -> setValueText(average), error -> Log.d("wildrank", this.getClass().getName()));

    }
}
