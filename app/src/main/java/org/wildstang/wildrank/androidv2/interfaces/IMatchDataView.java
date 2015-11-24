package org.wildstang.wildrank.androidv2.interfaces;

import com.couchbase.lite.Document;

import java.util.List;

public interface IMatchDataView {
    public void calculateFromDocuments(List<Document> documents);

    public void clearValue();
}
