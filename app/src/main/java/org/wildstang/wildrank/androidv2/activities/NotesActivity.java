package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.fragments.NotesSixFragment;

import java.io.IOException;

/**
 * Created by Liam on 2/21/2015.
 */
public class NotesActivity extends ActionBarActivity {
    private static String matchKey;
    private static String[] teamKeys;
    private static String[] notes;
    private Toolbar toolbar;
    private NotesSixFragment sixFrag;

    public static Intent createIntent(Context context, String matchKey, String[] teams) {
        Intent i = new Intent(context, NotesActivity.class);
        i.putExtra("match_key", matchKey);
        i.putExtra("team_keys", teams);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        matchKey = extras.getString("match_key");
        teamKeys = extras.getStringArray("team_keys");

        setContentView(R.layout.activity_notes);

        String team = PreferenceManager.getDefaultSharedPreferences(this).getString("assignedTeam", "red_1");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE);
        if (team.contains("red")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_red));

        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }


        ((TextView) findViewById(R.id.match_number)).setText("" + Utilities.matchNumberFromMatchKey(matchKey));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < teamKeys.length; i++) {
            if (i < teamKeys.length - 1) {
                sb.append(teamKeys[i] + ", ");
            } else {
                sb.append(teamKeys[i]);
            }

        }
        ((TextView) findViewById(R.id.team_numbers)).setText("" + Utilities.teamNumberFromTeamKey(sb.toString()));

        sixFrag = NotesSixFragment.newInstance(teamKeys);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.space, sixFrag);
        ft.commit();
    }

    public void finishScouting() {
        notes = sixFrag.getNotes();
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

    public void promptSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save");
        builder.setMessage("Would you like to save before exiting? (Press outside this dialog to cancel)");
        builder.setPositiveButton("Yes", (dialog, which) -> finishScouting());
        builder.setNegativeButton("No", (dialog, which) -> NotesActivity.this.finish());
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
