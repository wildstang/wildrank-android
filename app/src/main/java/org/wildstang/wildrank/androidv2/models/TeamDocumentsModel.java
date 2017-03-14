package org.wildstang.wildrank.androidv2.models;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.io.IOException;
import java.util.List;

/**
 * Container class for all the documents associated with a team. Currently holds the team document,
 * the pit scouting document, and a list of documents for each match the team has been scouted in.
 */
public class TeamDocumentsModel {

    private Document mTeamDoc;
    private Document mPitDoc;
    private List<Document> mMatchDocs;

    public TeamDocumentsModel(Document teamDoc, Document pitDoc, List<Document> matchDocs) {
        mTeamDoc = teamDoc;
        mPitDoc = pitDoc;
        mMatchDocs = matchDocs;
    }

    public static TeamDocumentsModel from(Context context, String teamKey) {
        try {
            DatabaseManager db = DatabaseManager.getInstance(context);
            Document teamDocument = db.getTeamFromKey(teamKey);
            Document pitDocument = db.getInternalDatabase().getExistingDocument("pit:" + teamKey);
            List<Document> matchDocuments = db.getMatchResultsForTeam(teamKey);
            return new TeamDocumentsModel(teamDocument, pitDocument, matchDocuments);
        } catch (CouchbaseLiteException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Document getTeamDocument() {
        return mTeamDoc;
    }

    public Document getPitDocument() {
        return mPitDoc;
    }

    public List<Document> getMatchDocuments() {
        return mMatchDocs;
    }
}
