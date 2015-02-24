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
import org.wildstang.wildrank.androidv2.fragments.NotesFragment;
import org.wildstang.wildrank.androidv2.fragments.NotesSixFragment;

import java.io.IOException;

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
    private NotesFragment frag;

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
        ft.add(R.id.space, sixFrag);
        ft.commit();

        listenForPress();
    }
    
    public void finishScouting()
    {
        try
        {
            DatabaseManager.getInstance(this).saveNotes(teams, notes);
        } catch (CouchbaseLiteException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finish();
    }

    public void promptSave()
    {
        if(!sixMode)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.space, sixFrag);
            ft.commit();
            sixMode = true;
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            promptSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        promptSave();
    }

    int i;
    public void listenForPress()
    {
        if(sixMode)
        {
            for(i = 0; i < sixFrag.boxes.size(); i++)
            {
                (new Thread()
                {
                    @Override
                    public void run()
                    {
                        while(!sixFrag.boxes.get(i).isPressed());
                        frag = new NotesFragment(teams[i]);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.add(R.id.space, frag);
                        ft.commit();
                        sixMode = false;
                    }
                }).start();
            }
        }
        else
        {
            (new Thread()
            {
                @Override
                public void run()
                {
                    while(!frag.box.isPressed());
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.space, sixFrag);
                    ft.commit();
                    sixMode = true;
                }
            }).start();
        }
    }
}
