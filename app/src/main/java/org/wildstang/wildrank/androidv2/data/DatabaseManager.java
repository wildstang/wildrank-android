package org.wildstang.wildrank.androidv2.data;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.UnsavedRevision;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathan on 1/19/2015.
 */
public class DatabaseManager {

    private static DatabaseManager instance;

    private Manager manager;
    private Database database;

    public static DatabaseManager getInstance(Context context) throws CouchbaseLiteException, IOException {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    private DatabaseManager(Context context) throws IOException, CouchbaseLiteException {
        manager = new Manager(new com.couchbase.lite.android.AndroidContext(context.getApplicationContext()), Manager.DEFAULT_OPTIONS);
        database = manager.getDatabase(DatabaseManagerConstants.DB_NAME);

        com.couchbase.lite.View matchViewByNumber = database.getView(DatabaseManagerConstants.MATCH_LIST_VIEW_BY_NUMBER);
        matchViewByNumber.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object docType = document.get(DatabaseManagerConstants.DOC_TYPE);
                if (docType != null) {
                    Log.d("wildrank", "type not null! " + docType.toString());
                    if (docType.toString().equals(DatabaseManagerConstants.MATCH_TYPE)) {
                        Log.d("wildrank", "emitting! " + document.get(DatabaseManagerConstants.MATCH_KEY));
                        emitter.emit(document.get("match_number"), null);
                    }
                }
            }
        }, "8");

        com.couchbase.lite.View matchViewByKey = database.getView(DatabaseManagerConstants.MATCH_LIST_VIEW_BY_KEY);
        matchViewByKey.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object docType = document.get(DatabaseManagerConstants.DOC_TYPE);
                if (docType != null) {
                    Log.d("wildrank", "type not null! " + docType.toString());
                    if (docType.toString().equals(DatabaseManagerConstants.MATCH_TYPE)) {
                        Log.d("wildrank", "emitting! " + document.get(DatabaseManagerConstants.MATCH_KEY));
                        emitter.emit(document.get("key"), null);
                    }
                }
            }
        }, "1");

        com.couchbase.lite.View matchResultsView = database.getView(DatabaseManagerConstants.MATCH_RESULT_VIEW);
        matchResultsView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object docType = document.get(DatabaseManagerConstants.DOC_TYPE);
                if (docType != null) {
                    if (docType.toString().equals(DatabaseManagerConstants.MATCH_RESULT_TYPE)) {
                        emitter.emit(document.get("match_key") + ":" + document.get("team_number"), null);
                    }
                }
            }
        }, "1");
    }

    public Manager getManager() {
        return manager;
    }

    /*
     * Matches
     */

    public Query getAllMatches() {
        Query query = database.getView(DatabaseManagerConstants.MATCH_LIST_VIEW_BY_NUMBER).createQuery();
        query.setDescending(false);
        return query;
    }

    public Document getMatchFromKey(String matchKey) throws CouchbaseLiteException {
        Query query = database.getView(DatabaseManagerConstants.MATCH_LIST_VIEW_BY_KEY).createQuery();
        query.setDescending(false);
        query.setStartKey(matchKey);
        query.setEndKey(matchKey);
        QueryEnumerator results = query.run();
        if (results.getCount() > 0) {
            return results.getRow(0).getDocument();
        } else {
            return null;
        }
    }

    /*
    Match results
     */

    public void saveMatchResults(String matchKey, String teamKey, Map<String, Object> data) throws CouchbaseLiteException {
        Document document = database.getDocument(matchKey + ":" + teamKey);
        UnsavedRevision revision = document.createRevision();
        HashMap<String, Object> matchResults = new HashMap<>();
        matchResults.put("match_key", matchKey);
        matchResults.put("team_key", teamKey);
        matchResults.put("data", data);
        revision.setProperties(matchResults);
        revision.save();
    }

    public Document getMatchResults(String matchKey, String teamKey) {
        return database.getDocument(matchKey + ":" + teamKey);
    }

    public boolean isMatchScouted(String matchKey, String teamKey) {
        return (database.getExistingDocument(matchKey + ":" + teamKey) != null);
    }

    public Database getDatabase() {
        return database;
    }
}
