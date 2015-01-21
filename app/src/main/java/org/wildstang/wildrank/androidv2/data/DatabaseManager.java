package org.wildstang.wildrank.androidv2.data;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;

import java.io.IOException;
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

        com.couchbase.lite.View matchView = database.getView(DatabaseManagerConstants.MATCH_LIST_VIEW);
        matchView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Log.d("wildrank", "mapping!");

                Object docType = document.get(DatabaseManagerConstants.DOC_TYPE);
                if (docType != null) {
                    Log.d("wildrank", "type not null! " + docType.toString());
                    if (docType.toString().equals(DatabaseManagerConstants.MATCH_TYPE)) {
                        Log.d("wildrank", "emitting! " + document.get(DatabaseManagerConstants.MATCH_KEY));
                        emitter.emit(/*document.get(DatabaseManagerConstants.MATCH_KEY)*/document.get("match_number"), null);
                    }
                }
            }
        }, "7");
    }

    public Manager getManager() {
        return manager;
    }

    /*
     * Matches
     */

    public Query getAllMatches() {
        Query query = database.getView(DatabaseManagerConstants.MATCH_LIST_VIEW).createQuery();
        query.setDescending(false);
        return query;
    }

    public Database getDatabase() {
        return database;
    }
}
