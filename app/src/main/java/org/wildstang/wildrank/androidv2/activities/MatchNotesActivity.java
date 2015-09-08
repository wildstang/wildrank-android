package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.fragments.MatchNotesFragment;

import java.io.IOException;

/**
 * Provides a way to take notes on all the teams in a specific match
 */
public class MatchNotesActivity extends AppCompatActivity {
    private static String matchKey;
    private static String[] teamKeys;
    private static String[] notes;
    private Toolbar toolbar;
    private MatchNotesFragment notesFragment;

    // Used to pass the teams and the match that the user is scouting into the activity
    public static Intent createIntent(Context context, String matchKey, String[] teams) {
        Intent i = new Intent(context, MatchNotesActivity.class);
        i.putExtra("match_key", matchKey);
        i.putExtra("team_keys", teams);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notes);

        Bundle extras = getIntent().getExtras();
        matchKey = extras.getString("match_key", null);
        teamKeys = extras.getStringArray("team_keys");

        if (matchKey == null || teamKeys == null) {
            throw new IllegalStateException("MatchNotesActivity must be created with match_key and team_keys a extras.");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sets the color of the toolbar based on what alliance is selected in the settings
        String team = Utilities.getAssignedTeam(this);
        toolbar.setTitleTextColor(Color.WHITE);
        if (team.contains("red")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_red));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }

        // Put the team numbers of the teams that are being scouted in the toolbar
        ((TextView) findViewById(R.id.match_number)).setText("" + Utilities.matchNumberFromMatchKey(matchKey));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < teamKeys.length; i++) {
            if (i < teamKeys.length - 1) {
                sb.append(teamKeys[i] + ", ");
            } else {
                sb.append(teamKeys[i]);
            }

        }

        // Put the match number of the match that is being scouted in the toolbar
        ((TextView) findViewById(R.id.team_numbers)).setText("" + sb.toString());

        notesFragment = MatchNotesFragment.newInstance(teamKeys); //passes in the team keys to the fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, notesFragment);
        ft.commit();
    }

    // Saves the notes to the proper document in the database
    public void saveNotes() {
        notes = notesFragment.getNotes();

        try {
            for (int i = 0; i < teamKeys.length; i++) {
                DatabaseManager.getInstance(this).saveNotes(teamKeys[i], notes[i], this);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();
    }

    // Creates dialog asking if you want to save the notes you just took
    public void promptSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save");
        builder.setMessage("Would you like to save before exiting? (Press outside this dialog to cancel)");
        builder.setPositiveButton("Yes", (dialog, which) -> saveNotes());
        builder.setNegativeButton("No", (dialog, which) -> MatchNotesActivity.this.finish());
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            promptSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        promptSave();
    }
}
