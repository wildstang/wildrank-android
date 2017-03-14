package org.wildstang.wildrank.androidv2.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.fragments.MatchStrategyFragment;

public class MatchStrategyActivity extends AppCompatActivity {

    private static String matchKey;
    private Toolbar toolbar;
    public static final String MATCH_KEY = "match_keys";
    private MatchStrategyFragment stratFrag;

    public static Intent createIntent(Context context, String matchKey, String[] teams) {
        Intent i = new Intent(context, MatchStrategyActivity.class);
        i.putExtra(MATCH_KEY, matchKey);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        matchKey = extras.getString(MATCH_KEY);

        setContentView(R.layout.activity_match_strategy);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String team = PreferenceManager.getDefaultSharedPreferences(this).getString("assignedTeam", "red_1");
        toolbar.setTitleTextColor(Color.WHITE);
        if (team.contains("red")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_red));

        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }

        ((TextView) findViewById(R.id.match_number)).setText("" + Utilities.matchNumberFromMatchKey(matchKey));

        Fragment fragment = MatchStrategyFragment.newInstance(matchKey);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}