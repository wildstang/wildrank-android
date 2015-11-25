package org.wildstang.wildrank.androidv2.interfaces;

import com.couchbase.lite.Document;

import java.util.List;

public interface IMatchDataView {
    void updateDocuments(List<Document> documents);

    void clearValue();
}
