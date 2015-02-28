package org.wildstang.wildrank.androidv2.data;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                    if (docType.toString().equals(DatabaseManagerConstants.MATCH_TYPE)) {
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
                    if (docType.toString().equals(DatabaseManagerConstants.MATCH_TYPE)) {
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

        View usersView = database.getView(DatabaseManagerConstants.USER_VIEW_BY_ID);
        usersView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object docType = document.get(DatabaseManagerConstants.DOC_TYPE);
                if (docType != null) {
                    if (docType.toString().equals(DatabaseManagerConstants.USER_TYPE)) {
                        emitter.emit(document.get("id"), null);
                    }
                }
            }
        }, "1");

        View teamsView = database.getView(DatabaseManagerConstants.TEAM_LIST_VIEW_BY_NUMBER);
        teamsView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object docType = document.get(DatabaseManagerConstants.DOC_TYPE);
                if (docType != null) {
                    if (docType.toString().equals(DatabaseManagerConstants.TEAM_TYPE)) {
                        emitter.emit(document.get("team_number"), null);
                    }
                }
            }
        }, "1");

        View pitResultsView = database.getView(DatabaseManagerConstants.PIT_RESULTS_VIEW);
        pitResultsView.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object docType = document.get(DatabaseManagerConstants.DOC_TYPE);
                if (docType != null) {
                    if (docType.toString().equals(DatabaseManagerConstants.PIT_RESULTS_TYPE)) {
                        emitter.emit(document.get("team_key"), null);
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
     * Match results
     */

    public void saveMatchResults(MatchResultsModel matchResults) throws CouchbaseLiteException {
        Document document = database.getDocument(matchResults.getMatchKey() + ":" + matchResults.getTeamKey());
        UnsavedRevision revision = document.createRevision();
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("type", DatabaseManagerConstants.MATCH_RESULT_TYPE);
        properties.put("users", matchResults.getUserIds());
        properties.put("match_key", matchResults.getMatchKey());
        properties.put("team_key", matchResults.getTeamKey());
        properties.put("data", matchResults.getData());
        revision.setProperties(properties);
        revision.save();
    }

    public Document getMatchResults(String matchKey, String teamKey) {
        return database.getDocument(matchKey + ":" + teamKey);
    }

    public boolean isMatchScouted(String matchKey, String teamKey) {
        return (database.getExistingDocument(matchKey + ":" + teamKey) != null);
    }

    /*
     * Users
     */

    public Document getUserById(String id) throws CouchbaseLiteException {
        Query query = database.getView(DatabaseManagerConstants.USER_VIEW_BY_ID).createQuery();
        query.setStartKey(id);
        query.setEndKey(id);
        QueryEnumerator results = query.run();
        if (results.getCount() > 0) {
            return results.getRow(0).getDocument();
        } else {
            return null;
        }
    }

    /*
     * Teams
     */
    public Query getAllTeams() {
        Query query = database.getView(DatabaseManagerConstants.TEAM_LIST_VIEW_BY_NUMBER).createQuery();
        query.setDescending(false);
        return query;
    }

    /*
     * Pit Scouting
     */

    public boolean isTeamPitScouted(String teamKey) {
        return (database.getExistingDocument("pit:" + teamKey) != null);
    }

    public void savePitResults(PitResultsModel pitResults) throws CouchbaseLiteException {
        Document document = database.getDocument("pit:" + pitResults.getTeamKey());
        UnsavedRevision revision = document.createRevision();
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("type", DatabaseManagerConstants.PIT_RESULTS_TYPE);
        properties.put("users", pitResults.getUserIds());
        properties.put("team_key", pitResults.getTeamKey());
        properties.put("data", pitResults.getData());
        revision.setProperties(properties);
        revision.save();
    }

    public void saveNotes(String[] teams, String[] notes) throws CouchbaseLiteException
    {
        for(int i = 0; i < teams.length; i++)
        {
            if(!notes[i].equals(""))
            {
                Boolean existed = (database.getExistingDocument("notes:" + teams[i]) != null);
                Document document = database.getDocument("notes:" + teams[i]);
                UnsavedRevision revision = document.createRevision();

                HashMap<String, Object> properties = new HashMap<>();
                properties.put("type", DatabaseManagerConstants.NOTES_RESULTS_TYPE);
                properties.put("users", "to be added");
                properties.put("team_key", teams[i]);

                List<String> notesList;
                if(existed)
                {
                    notesList = (ArrayList<String>) document.getProperties().get("notes");
                }
                else
                {
                    notesList = new ArrayList<>();
                }

                notesList.add(notes[i]);
                properties.put("notes", notesList);

                revision.setProperties(properties);
                revision.save();
            }
        }
    }

    public String[] getNotes(String team)
    {
        Document document = database.getDocument("notes:" + team);
        List<String> notesList;
            if(document.getProperties().containsKey("notes"))
            {
                notesList = (ArrayList<String>) document.getProperties().get("notes");
            }
            else
            {
                notesList = new ArrayList<>();
            }
        return notesList.toArray(new String[notesList.size()]);
    }

    public Database getDatabase() {
        return database;
    }
}
