package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.UserHelper;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.adapters.MatchScoutFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.adapters.PitScoutFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.data.PitResultsModel;
import org.wildstang.wildrank.androidv2.fragments.ScoutingFragment;
import org.wildstang.wildrank.androidv2.views.SlidingTabs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 1/22/2015.
 */
public class ScoutPitActivity extends ActionBarActivity {

    public static final String EXTRA_TEAM_KEY = "team_key";

    private ViewPager pager;
    private PitScoutFragmentPagerAdapter adapter;
    private SlidingTabs tabs;
    private Toolbar toolbar;

    private String teamKey;

    public static Intent createIntent(Context context, String teamKey) {
        Intent i = new Intent(context, ScoutPitActivity.class);
        i.putExtra(EXTRA_TEAM_KEY, teamKey);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        teamKey = extras.getString(EXTRA_TEAM_KEY);

        setContentView(R.layout.activity_scout_pit);

        ((TextView) findViewById(R.id.team_number)).setText("" + Utilities.teamNumberFromTeamKey(teamKey));

        findViewById(android.R.id.content).setKeepScreenOn(true);

        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setOffscreenPageLimit(10);

        adapter = new PitScoutFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void finishScouting() {
        List<ScoutingFragment> fragments = adapter.getAllFragments();
        Map<String, Object> data = new HashMap<>();
        for (ScoutingFragment fragment : fragments) {
            fragment.writeContentsToMap(data);
        }

        try {
            PitResultsModel results = new PitResultsModel(UserHelper.getLoggedInUsersAsArray(this), teamKey, data);
            DatabaseManager.getInstance(this).savePitResults(results);
            //Document doc = DatabaseManager.getInstance(this).ge(matchKey, teamKey);
            //Log.d("wildrank", doc.getProperties().toString());
            Toast.makeText(this, "Match results saved successfully.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving match results! Check LogCat.", Toast.LENGTH_LONG).show();
        }

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm exit");
            builder.setMessage("If you exit without saving, all data will be lost. Do you still want to exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ScoutPitActivity.this.finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
