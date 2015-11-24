package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.UserHelper;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.adapters.PitScoutFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.data.PitResultsModel;
import org.wildstang.wildrank.androidv2.fragments.ScoutingFragment;
import org.wildstang.wildrank.androidv2.views.SlidingTabs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoutPitActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_KEY = "team_key";
    public String teamKey;
    private ViewPager pager;
    private PitScoutFragmentPagerAdapter adapter;
    private SlidingTabs tabs;
    private Toolbar toolbar;

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

        String team = PreferenceManager.getDefaultSharedPreferences(this).getString("assignedTeam", "red_1");

        if (team.contains("red")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_red));
            tabs.setBackgroundColor(getResources().getColor(R.color.material_red));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue));
            tabs.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }

        // determine if this team has already been scouted. If it has, load the existing data.
        try {
            Log.d("wildrank", "team key: " + teamKey);
            if (DatabaseManager.getInstance(this).isTeamPitScouted(teamKey)) {
                Log.d("wildrank", "existing data!");
                Document pitDoc = DatabaseManager.getInstance(this).getInternalDatabase().getExistingDocument("pit:" + teamKey);
                Map<String, Object> map = (Map<String, Object>) pitDoc.getProperty("data");
                Log.d("wildrank", map.toString());
                restoreFromMap(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load existing pit data.", Toast.LENGTH_LONG).show();
        }
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

    public void restoreFromMap(Map<String, Object> map) {
        List<ScoutingFragment> fragments = adapter.getAllFragments();
        Log.d("wildrank", "length of fragments: " + fragments.size());
        for (ScoutingFragment fragment : fragments) {
            Log.d("wildrank", "restoring fragment!");
            fragment.restoreContentsFromMap(map);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm exit");
            builder.setMessage("If you exit without saving, all data will be lost. Do you still want to exit?");
            builder.setPositiveButton("Yes", (dialog, which) -> ScoutPitActivity.this.finish());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
