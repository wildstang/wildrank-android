package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * For taking notes for all the teams in a specific match
 */
public class MatchNotesActivity extends AppCompatActivity {
    private static String matchKey;
    private static String[] teamKeys;
    private static String[] notes;
    private Toolbar toolbar;
    private MatchNotesFragment sixFrag;

    //Used to pass the teams and the match that the user is scouting into the activity
    public static Intent createIntent(Context context, String matchKey, String[] teams) {
        Intent i = new Intent(context, MatchNotesActivity.class);
        i.putExtra("match_key", matchKey);
        i.putExtra("team_keys", teams);
        return i;
    }

    //Ran when the activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //gets the team keys and the match key of the match that is being scouted
        Bundle extras = getIntent().getExtras();
        matchKey = extras.getString("match_key");
        teamKeys = extras.getStringArray("team_keys");

        //sets the view
        setContentView(R.layout.activity_notes);

        //sets up the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //sets the color of the toolbar based on what alliance is selected in the settings
        String team = PreferenceManager.getDefaultSharedPreferences(this).getString("assignedTeam", "red_1");
        toolbar.setTitleTextColor(Color.WHITE);
        if (team.contains("red")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_red));

        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }

        //puts the team numbers of the teams that are being scouted in the toolbar
        ((TextView) findViewById(R.id.match_number)).setText("" + Utilities.matchNumberFromMatchKey(matchKey));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < teamKeys.length; i++) {
            if (i < teamKeys.length - 1) {
                sb.append(teamKeys[i] + ", ");
            } else {
                sb.append(teamKeys[i]);
            }

        }
        //put the match number of the match that is being scouted in the toolbar
        ((TextView) findViewById(R.id.team_numbers)).setText("" + sb.toString());

        //creates the fragment that contains the view
        sixFrag = MatchNotesFragment.newInstance(teamKeys);//passes in the team keys to the fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.space, sixFrag);
        ft.commit();
    }

    //Saves the notes to the proper document in the database
    public void finishScouting() {
        //gets the notes in an array
        notes = sixFrag.getNotes();
        try {
            //tells the data base to save them
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

    //Creates dialog asking if you want to save the notes you just took
    public void promptSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save");
        builder.setMessage("Would you like to save before exiting? (Press outside this dialog to cancel)");
        //If you press yes save the notes
        builder.setPositiveButton("Yes", (dialog, which) -> finishScouting());
        //If you press no don't do anything with them
        builder.setNegativeButton("No", (dialog, which) -> MatchNotesActivity.this.finish());
        //If you click outside of the dialog it will cancel
        builder.show();
    }

    //Listens for the upper left home button to be pressed
    //If it is it prompts to save the notes
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            promptSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Listens for the back button to be pressed
    //If it is it prompts to save the notes
    @Override
    public void onBackPressed() {
        promptSave();
    }
}
