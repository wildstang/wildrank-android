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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.fragments.NotesSixFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liam on 2/21/2015.
 */
public class NotesActivity extends ActionBarActivity
{
    private Toolbar toolbar;
    private String teamKeys;
    private String matchKey;
    private Boolean sixMode = true;
    private String[] teams;
    private String[] notes;

    private NotesSixFragment sixFrag;

    public static Intent createIntent(Context context, String matchKey, String teamKeys, String[] teams)
    {
        Intent i = new Intent(context, NotesActivity.class);
        i.putExtra("team_keys", teamKeys);
        i.putExtra("match_key", matchKey);
        i.putExtra("teams_key", teams);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        teamKeys = extras.getString("team_keys");
        matchKey = extras.getString("match_key");
        teams = extras.getStringArray("teams_key");

        setContentView(R.layout.activity_notes);

        String team = PreferenceManager.getDefaultSharedPreferences(this).getString("assignedTeam", "red_1");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        toolbar.setTitleTextColor(Color.WHITE);
        if (team.contains("red"))
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color./*material_*/red));

        } else
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color./*material_*/blue));
        }

        ((TextView) findViewById(R.id.match_number)).setText("" + Utilities.matchNumberFromMatchKey(matchKey));
        ((TextView) findViewById(R.id.team_numbers)).setText("" + Utilities.teamNumberFromTeamKey(teamKeys));

        sixFrag = new NotesSixFragment(teams);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.space, sixFrag, "myFragmentTag");
        ft.commit();
    }
    
    public void finishScouting()
    {
        Database database = null;
        try
        {
            database = DatabaseManager.getInstance(this).getDatabase();
        } catch (CouchbaseLiteException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        for (int i = 0; i < teams.length; i++)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("note", sixFrag.boxes.get(i).getNote());

            Document document = database.getDocument("user:" + teams[i]);
            UnsavedRevision revision = document.createRevision();
            revision.setProperties(map);
            try
            {
                revision.save();
            } catch (CouchbaseLiteException e)
            {
                e.printStackTrace();
            }
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            if(!sixMode)
            {
                //go to 6 mode
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Save");
                builder.setMessage("Would you like to save before exiting?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finishScouting();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        NotesActivity.this.finish();
                    }
                });
                builder.show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
