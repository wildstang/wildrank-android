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
import org.wildstang.wildrank.androidv2.adapters.MatchScoutFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.data.MatchResultsModel;
import org.wildstang.wildrank.androidv2.fragments.ScoutingFragment;
import org.wildstang.wildrank.androidv2.views.SlidingTabs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoutMatchActivity extends AppCompatActivity {

    public static final String EXTRA_MATCH_KEY = "match_key";
    public static final String EXTRA_TEAM_KEY = "team_key";
    public static final String EXTRA_ALLIANCE_COLOR = "alliance_color";
    public static final String EXTRA_ALLIANCE_COLOR_RED = "red";
    public static final String EXTRA_ALLIANCE_COLOR_BLUE = "blue";
    public String teamKey;
    private ViewPager pager;
    private MatchScoutFragmentPagerAdapter adapter;
    private SlidingTabs tabs;
    private Toolbar toolbar;
    private String matchKey;
    private String allianceColor;

    public static Intent createIntent(Context context, String matchKey, String teamKey, String allianceColor) {
        Intent i = new Intent(context, ScoutMatchActivity.class);
        i.putExtra(EXTRA_MATCH_KEY, matchKey);
        i.putExtra(EXTRA_TEAM_KEY, teamKey);
        i.putExtra(EXTRA_ALLIANCE_COLOR, allianceColor);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        matchKey = extras.getString(EXTRA_MATCH_KEY);
        teamKey = extras.getString(EXTRA_TEAM_KEY);
        allianceColor = extras.getString(EXTRA_ALLIANCE_COLOR);

        setContentView(R.layout.activity_scout_match);

        ((TextView) findViewById(R.id.match_number)).setText("" + Utilities.matchNumberFromMatchKey(matchKey));
        ((TextView) findViewById(R.id.team_number)).setText("" + Utilities.teamNumberFromTeamKey(teamKey));


        findViewById(android.R.id.content).setKeepScreenOn(true);

        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setOffscreenPageLimit(10);

        adapter = new MatchScoutFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabs = (SlidingTabs) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView alliance = (TextView) findViewById(R.id.alliance);
        String team = PreferenceManager.getDefaultSharedPreferences(this).getString("assignedTeam", "red_1");

        if (team.contains("red")) {
            alliance.setText("Red");
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_red));
            tabs.setBackgroundColor(getResources().getColor(R.color.material_red));
        } else {
            alliance.setText("Blue");
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue));
            tabs.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }
    }

    public void finishScouting() {
        List<ScoutingFragment> fragments = adapter.getAllFragments();
        Map<String, Object> data = new HashMap<>();
        for (ScoutingFragment fragment : fragments) {
            fragment.writeContentsToMap(data);
        }

        try {
            MatchResultsModel results = new MatchResultsModel(UserHelper.getLoggedInUsersAsArray(this), matchKey, teamKey, data);
            DatabaseManager.getInstance(this).saveMatchResults(results);
            Document doc = DatabaseManager.getInstance(this).getMatchResults(matchKey, teamKey);
            Log.d("wildrank", doc.getProperties().toString());
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
            builder.setPositiveButton("Yes", (dialog, which) -> ScoutMatchActivity.this.finish());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
