package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.Context;
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
import org.wildstang.wildrank.androidv2.fragments.TeamNotesFragment;

import java.io.IOException;

/**
 * For taking notes on a specific team
 */
public class TeamNotesActivity extends ActionBarActivity {
    //Team key (frc[team-number]) and note text
    private static String teamKey;
    private static String note;

    //The bar at the top
    private Toolbar toolbar;
    //Fragment that contains the view
    private TeamNotesFragment frag;

    //Used to pass the team that the user is scouting into the activity
    public static Intent createIntent(Context context, String team) {
        Intent i = new Intent(context, TeamNotesActivity.class);
        i.putExtra("team_key", team);
        return i;
    }

    //Ran when the activity is started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //gets the team key of the team that is being scouted
        Bundle extras = getIntent().getExtras();
        teamKey = extras.getString("team_key");

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

        //puts the team number of the team that is being scouted in the toolbar
        ((TextView) findViewById(R.id.team_numbers)).setText("" + Utilities.teamNumberFromTeamKey(teamKey));

        //creates the fragment that contains the view
        frag = TeamNotesFragment.newInstance(teamKey); //passes in the team key to the fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.space, frag);
        ft.commit();
    }

    //Saves the notes to the proper document in the database
    public void finishScouting() {
        //gets the notes
        note = frag.getNote();
        try {
            //tells the data base to save it
            DatabaseManager.getInstance(this).saveNotes(teamKey, note, this);
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
        builder.setNegativeButton("No", (dialog, which) -> TeamNotesActivity.this.finish());
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
