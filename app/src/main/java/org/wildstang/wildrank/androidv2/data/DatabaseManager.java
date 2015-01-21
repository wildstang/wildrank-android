package org.wildstang.wildrank.androidv2.data;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;

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
                if (document.get(DatabaseManagerConstants.DOC_TYPE).equals(DatabaseManagerConstants.MATCH_TYPE)) {
                    emitter.emit(document.get(DatabaseManagerConstants.MATCH_KEY), null);
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

    public LiveQuery getAllMatches() {
        return database.getView(DatabaseManagerConstants.MATCH_LIST_VIEW).createQuery().toLiveQuery();
    }

    public Database getDatabase() {
        return database;
    }
}
