package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Context;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.SyncUtilities;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.data.DatabaseManagerConstants;

import java.io.File;
import java.util.Iterator;

/**
 * Created by Nathan on 2/16/2015.
 */
public class SyncActivity extends ActionBarActivity {

    TextView internalDatabaseContents;
    TextView externalDatabaseContents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sync);

        internalDatabaseContents = (TextView) findViewById(R.id.internal_contents);
        externalDatabaseContents = (TextView) findViewById(R.id.external_contents);

        beginSync();
    }

    private void beginSync() {
        if (!SyncUtilities.isFlashDriveConnected()) {
            showDriveNotMountedWarning();
        } else {
            try {
                syncDatabases();
                Toast.makeText(this, "Sync complete!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error syncing databases. Check logcat.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showDriveNotMountedWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect flash drive");
        builder.setMessage("Please connect a flash drive that has been set up using the desktop application.");
        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                beginSync();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SyncActivity.this.finish();
            }
        });
        builder.create().show();
    }

    /**
     * Syncs the internal and external databases
     */
    private void syncDatabases() throws Exception {
        /* First, read the current states and last known states into memory */

        // Custom Couchbase Manager that points to the flash drive
        Context context = new AndroidContext(SyncActivity.this) {
            @Override
            public File getFilesDir() {
                return new File(Utilities.getExternalRootDirectory() + "/");
            }
        };
        Database externalDatabase = new Manager(context, Manager.DEFAULT_OPTIONS).getDatabase(DatabaseManagerConstants.DB_NAME);
        externalDatabase.open();
        Database internalDatabase = DatabaseManager.getInstance(this).getDatabase();

        DatabaseManager.DatabaseState lastExternalState = DatabaseManager.getInstance(this).getLastKnownExternalState();
        DatabaseManager.DatabaseState lastInternalState = DatabaseManager.getInstance(this).getLastKnownInternalState();

        // Now, iterate through all the current records in the internal database
        Query query = internalDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        QueryEnumerator result = query.run();
        Log.d("wildrank", "QueryEnumerator length: " + result.getCount());
        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            Document doc = row.getDocument();
            String docId = doc.getId();
            Log.d("wildrank", "current doc id: " + docId);
            if (docId.equals(DatabaseManagerConstants.LAST_KNOWN_INTERNAL_DATABASE_STATE_DOCUMENT_ID) || docId.equals(DatabaseManagerConstants.LAST_KNOWN_EXTERNAL_DATABASE_STATE_DOCUMENT_ID)) {
                // Skip the state tracking things
                continue;
            }
            // Check if the record exists in the last known local state
            if (!lastInternalState.documentExists(docId)) {
                // If not, this is a new document that should immediately be synced to the external database
                Document document = externalDatabase.getDocument(docId);
                UnsavedRevision revision = document.createRevision();
                revision.setProperties(doc.getProperties());
                revision.save();
            } else {
                // If it did exist locally at the time of the last sync, compare the revision IDs
                if (lastInternalState.getDocStateForId(docId).revisionId.equals(doc.getCurrentRevisionId())) {
                    // If the revision IDs are the same, no action is required
                    continue;
                } else {
                    // If they are not the same, then the local document has been modified since the time of the last sync
                    // Compare the revision IDs of the external document both currently and at the time of the last sync
                    String currentExternalRevisionId = externalDatabase.getDocument(docId).getCurrentRevisionId();
                    Log.d("wildrank", "current external rev id: " + currentExternalRevisionId);
                    String lastKnownExternalRevisionId = lastExternalState.getDocStateForId(doc.getId()).revisionId;
                    Log.d("wildrank", "current external revision id: " + currentExternalRevisionId + "; last known external revision id: " + lastKnownExternalRevisionId);
                    if (currentExternalRevisionId.equals(lastKnownExternalRevisionId)) {
                        // If they are the same, the document was not modified externally. Replace the external document with the updated internal one

                        Document document = externalDatabase.getDocument(docId);
                        UnsavedRevision revision = document.createRevision();
                        revision.setProperties(doc.getProperties());
                        revision.save();
                    } else {
                        // If they are not the same, we have a conflict. Prompt the user to resolve it. Perhaps just show the raw JSON objects and let them choose?
                    }
                }
            }

            // TODO: handle deletions
        }

        // Now we can sync the external database back to the internal one
        query = externalDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        result = query.run();
        Log.d("wildrank", "QueryEnumerator length: " + result.getCount());
        // Iterate through all the current records in the external database
        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            Document doc = row.getDocument();
            String docId = doc.getId();
            if (docId.equals(DatabaseManagerConstants.LAST_KNOWN_INTERNAL_DATABASE_STATE_DOCUMENT_ID) || docId.equals(DatabaseManagerConstants.LAST_KNOWN_EXTERNAL_DATABASE_STATE_DOCUMENT_ID)) {
                // Skip the state tracking things
                continue;
            }
            // Check if the document key exists in the internal database
            if (internalDatabase.getExistingDocument(docId) != null) {
                // If it does not, this is a new document and can be immediately synced to the internal database.
                Document document = internalDatabase.getDocument(docId);
                UnsavedRevision revision = document.createRevision();
                revision.setProperties(doc.getProperties());
                revision.save();
            } else {
                // If it does, compare the revision IDs of the internal and external documents
                if (internalDatabase.getDocument(docId).getCurrentRevisionId().equals(doc.getCurrentRevisionId())) {
                    // If they are the same, no further action is required
                } else {
                    // If they are not the same, then the external document has been modified since the last sync.
                    // If the internal document had been modified since the last sync, it would already have
                    // been synced in the first half of the syncing process. We can safely replace the internal
                    // document with the external one.
                    Document document = internalDatabase.getDocument(docId);
                    UnsavedRevision revision = document.createRevision();
                    revision.setProperties(doc.getProperties());
                    revision.save();
                }
            }

            // TODO: still need to handle deletions
        }

        externalDatabase.close();
    }


    private void readDatabases() {
        Log.d("wildrank", "reading databasees");
        try {

            Manager internalManager = DatabaseManager.getInstance(SyncActivity.this).getManager();

            // Custom Couchbase Manager that points to the flash drive
            Context context = new AndroidContext(SyncActivity.this) {
                @Override
                public File getFilesDir() {
                    return new File(Utilities.getExternalRootDirectory() + "/");
                }
            };
            Manager externalManager = new Manager(context, Manager.DEFAULT_OPTIONS);

            Database internalDatabase = internalManager.getDatabase(DatabaseManagerConstants.DB_NAME);
            Database externalDatabase = externalManager.getExistingDatabase(DatabaseManagerConstants.DB_NAME);
            externalDatabase.open();

            StringBuilder externalString = new StringBuilder();
            Query query = externalDatabase.createAllDocumentsQuery();
            query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
            QueryEnumerator result = query.run();
            Log.d("wildrank", "QueryEnumerator length: " + result.getCount());
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document doc = row.getDocument();
                externalString.append(doc.getProperties()).append("\n");
            }
            externalDatabase.close();
            externalDatabaseContents.setText(externalString.toString());

            StringBuilder internalString = new StringBuilder();
            query = internalDatabase.createAllDocumentsQuery();
            query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
            result = query.run();
            Log.d("wildrank", "QueryEnumerator length: " + result.getCount());
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document doc = row.getDocument();
                internalString.append(doc.getProperties()).append("\n");
            }
            internalDatabaseContents.setText(internalString.toString());

            Log.d("wildrank", internalDatabaseContents.toString());
            Log.d("wildrank", externalDatabaseContents.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading!", Toast.LENGTH_SHORT).show();
        }
    }
}
