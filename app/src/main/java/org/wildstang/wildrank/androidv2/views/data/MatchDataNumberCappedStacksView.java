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
        Observable<Integer> o = Observable.from(documents)
                .filter(doc -> (doc.getProperty("data") != null))
                .map(doc -> (Map<String, Object>) doc.getProperty("data"))
                .flatMap(data -> {
                    List<Map<String, Object>> stacks = (List<Map<String, Object>>) data.get("stacks");
                    return Observable.from(stacks);
                })
                .map(stack -> {
                    boolean includesBin = (boolean) stack.get(StackModel.HAS_BIN_KEY);
                    boolean binDropped = (boolean) stack.get(StackModel.BIN_DROPPED_KEY);
                    return (includesBin && !binDropped) ? 1 : 0;
                })
                .subscribeOn(Schedulers.computation());
        MathObservable.sumInteger(o)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sum -> setValueText("" + sum), error -> Log.d("wildrank", this.getClass().getName()));
    }
}
