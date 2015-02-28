package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.couchbase.lite.Context;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.data.DatabaseManagerConstants;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class AppSetupActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setup);

        findViewById(R.id.button).setOnClickListener(this);
    }

    private void beginDataLoad() {
        if (!isFlashDriveConnected()) {
            showExternalWarning();
        } else {
            // DO SHIT
            new SetupTask().execute();
        }

    }

    private void setupComplete() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_INTERNAL_STORAGE_SETTINGS), /*RESULT_CODE_UNMOUNT*/1);
        Toast.makeText(this, "Scroll down, press \"Unmount\", press back button.", Toast.LENGTH_LONG).show();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(HomeActivity.PREF_IS_APP_CONFIGURED, true).commit();
    }

    private boolean isFlashDriveConnected() {
        return new File(Utilities.getExternalRootDirectory()).exists();
    }

    private void showExternalWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect flash drive");
        builder.setMessage("Please connect a flash drive that has been set up using the desktop application.");
        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                beginDataLoad();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSetupActivity.this.finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button) {
            beginDataLoad();
            findViewById(R.id.button).setEnabled(false);
        }
    }

    private class SetupTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Manager internalManager = DatabaseManager.getInstance(AppSetupActivity.this).getManager();

                // Custom Couchbase Manager that points to the flash drive
                Context context = new AndroidContext(AppSetupActivity.this) {
                    @Override
                    public File getFilesDir() {
                        return new File(Utilities.getExternalRootDirectory() + "/");
                    }
                };
                Manager externalManager = new Manager(context, Manager.DEFAULT_OPTIONS);

                Database internalDatabase = internalManager.getDatabase(DatabaseManagerConstants.DB_NAME);
                Database externalDatabase = externalManager.getExistingDatabase(DatabaseManagerConstants.DB_NAME);

                // Copy everything into the internal database
                Query query = externalDatabase.createAllDocumentsQuery();
                query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
                QueryEnumerator result = query.run();
                Log.d("wildrank", "QueryEnumerator length: " + result.getCount());
                for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                    QueryRow row = it.next();
                    Document existingDoc = row.getDocument();
                    Log.d("wildrank", "Document contents: " + existingDoc.getProperties().toString());
                    Document doc = internalDatabase.getDocument(existingDoc.getId());
                    UnsavedRevision revision = doc.createRevision();
                    Map<String, Object> existingProperties = existingDoc.getProperties();
                    try {
                        revision.setProperties(existingProperties);
                        revision.save();
                    } catch (CouchbaseLiteException e) {
                        Log.d("Wildrank", "Error writing document");
                        e.printStackTrace();
                    }
                }
                externalDatabase.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            AppSetupActivity.this.setupComplete();
        }
    }
}
