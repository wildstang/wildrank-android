package org.wildstang.wildrank.androidv2.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.UserUtilities;
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
    public String teamKey;
    private ViewPager pager;
    private MatchScoutFragmentPagerAdapter adapter;
    private SlidingTabs tabs;
    private Toolbar toolbar;
    private String matchKey;

    public static Intent createIntent(Context context, String matchKey, String teamKey) {
        Intent i = new Intent(context, ScoutMatchActivity.class);
        i.putExtra(EXTRA_MATCH_KEY, matchKey);
        i.putExtra(EXTRA_TEAM_KEY, teamKey);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        matchKey = extras.getString(EXTRA_MATCH_KEY);
        teamKey = extras.getString(EXTRA_TEAM_KEY);

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

        String assignedTeam = Utilities.getAssignedTeam(this);
        if (assignedTeam.contains("red")) {
            alliance.setText(R.string.red);
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_red));
            tabs.setBackgroundColor(getResources().getColor(R.color.material_red));
        } else {
            alliance.setText(R.string.blue);
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
            MatchResultsModel results = new MatchResultsModel(UserUtilities.getLoggedInUsersAsArray(this), matchKey, teamKey, data);
            DatabaseManager.getInstance(this).saveMatchResults(results);
            Toast.makeText(this, R.string.match_scouting_save_successful, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.match_scouting_save_unsuccessful, Toast.LENGTH_LONG).show();
        }

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.confirm_exit_title);
            builder.setMessage(R.string.confirm_exit_message);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> ScoutMatchActivity.this.finish());
            builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
