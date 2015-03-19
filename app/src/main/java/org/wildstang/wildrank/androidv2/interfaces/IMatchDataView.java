package org.wildstang.wildrank.androidv2.interfaces;

import com.couchbase.lite.Document;

import java.util.List;

/**
 * Created by Nathan on 3/18/2015.
 */
public interface IMatchDataView {
    public void calculateFromDocuments(List<Document> documents);

    public void clearValue();
}
