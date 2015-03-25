package org.wildstang.wildrank.androidv2.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.UserHelper;
import org.wildstang.wildrank.androidv2.fragments.MatchScoutingMainFragment;
import org.wildstang.wildrank.androidv2.fragments.NotesMainFragment;
import org.wildstang.wildrank.androidv2.fragments.PitScoutingMainFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesMainFragment;


/**
 * Serves as the jumping-off point of the entire app. It utilizes a navigation drawer to jump
 * between various top-level screens, including match scouting, pit scouting, and data viewing.
 * <p/>
 * When you switch modes, the app swaps in the appropriate main fragment for the given mode. The
 * modes are defined in the enum Mode; new modes should be added there, and any setup that should be
 * done when that mode is selected should be defined in switchToMode().
 */
public class HomeActivity extends ActionBarActivity {

    public static final String PREF_IS_APP_CONFIGURED = "is_app_configured";

    private static final String[] MODE_NAMES;

    static {
        MODE_NAMES = new String[Mode.values().length];
        for (Mode mode : Mode.values()) {
            MODE_NAMES[mode.ordinal()] = mode.getTitle();
        }
    }
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView navigationDrawerList;
    private Toolbar toolbar;
    private Mode currentMode = null;

    @Override
    public void onResume() {
        super.onResume();

        // If we're resuming, the assigned team could have changed. Set up the color of the Toolbar again.
        setUpToolbar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean isAppConfigured = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_IS_APP_CONFIGURED, false);
        Log.d("wildrank", "isAppConfigured: " + isAppConfigured);

        boolean isLoggedIn = UserHelper.isUserLoggedIn(this);

        if (isAppConfigured) {
            // The app has been set up! Go ahead and display stuff.
            if (isLoggedIn) {
                // Default to match scouting mode
                switchToMode(Mode.MATCH_SCOUTING);
            } else {
                // A user needs to be looged in first before we can begin
                startActivity(new Intent(this, UserLoginActivity.class));
                finish();
            }
        } else {
            /* The app hasn't been setup yet. Launch the setup activity and finish this one.
             * After setup is finished, we will relaunch this one.
             */
            startActivity(new Intent(this, AppSetupActivity.class));
            finish();
        }

        setUpToolbar();

        drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        navigationDrawerList = (ListView) findViewById(R.id.navigation_drawer_list);
        setUpNavigationDrawer();
    }

    private void setUpToolbar() {
        toolbar.setTitleTextColor(Color.WHITE);

        String team = PreferenceManager.getDefaultSharedPreferences(this).getString("assignedTeam", "red_1");
        if (team.contains("red")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_red));

        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    public void setUpNavigationDrawer() {
        // set a custom shadow that overlays the main content when the drawer opens
        // drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        drawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                drawerLayout,                    /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        // Defer code dependent on restoration of previous instance state.
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        navigationDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected(position);
            }
        });
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MODE_NAMES);
        navigationDrawerList.setAdapter(adapter);
    }

    /**
     * Called when an item is selected from the navigation drawer.
     *
     * @param position the position of the selected item in the list. This should correspond to the
     *                 ordinal of the enum representing this mode.
     */
    private void onItemSelected(int position) {
        if (position > Mode.values().length - 1) {
            return;
        }
        Mode mode = Mode.values()[position];
        switchToMode(mode);
        drawerLayout.closeDrawers();
    }

    private void switchToMode(Mode mode) {
        if (currentMode != null) {
            if (currentMode == mode) {
                return;
            }
        }
        switch (mode) {
            case MATCH_SCOUTING:
                //Match scouting
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MatchScoutingMainFragment()).commit();
                getSupportActionBar().setTitle(MODE_NAMES[0]);
                break;
            case PIT_SCOUTING:
                // Pit scouting
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PitScoutingMainFragment()).commit();
                getSupportActionBar().setTitle(MODE_NAMES[1]);
                break;
            case NOTES:
                // Notes
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotesMainFragment()).commit();
                getSupportActionBar().setTitle(MODE_NAMES[2]);
                break;
            case TEAM_SUMMARIES:
                // Team Summaries
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TeamSummariesMainFragment()).commit();
                getSupportActionBar().setTitle(MODE_NAMES[3]);
                break;
            default:
                break;
        }
        currentMode = mode;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_sync) {
            startActivity(new Intent(this, SyncActivity.class));
            return true;
        } else if (id == R.id.action_log_out) {
            UserHelper.logOutAllUsers(this);
            startActivity(new Intent(this, UserLoginActivity.class));
            this.finish();
        } else if (id == R.id.action_add_user) {
            Intent i = new Intent(this, UserLoginActivity.class);
            // Don't create a new Home activity after the user is logged in; simply return to this one.
            i.putExtra(UserLoginActivity.EXTRA_CREATE_NEW_HOME, false);
            startActivity(i);
        } else if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Defines "modes" that can be switched to from the navigation drawer
    private enum Mode {
        MATCH_SCOUTING("Match scouting"),
        PIT_SCOUTING("Pit scouting"),
        NOTES("Notes"),
        TEAM_SUMMARIES("Team summaries");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }
}