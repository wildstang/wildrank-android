package org.wildstang.wildrank.androidv2.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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


public class HomeActivity extends ActionBarActivity {

    public static final String PREF_IS_APP_CONFIGURED = "is_app_configured";

    private static final String[] MODE_NAMES = {"Match Scouting", "Pit Scouting", "Notes"};

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView navigationDrawerList;

    private int currentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

        boolean isAppConfigured = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_IS_APP_CONFIGURED, false);

        boolean isLoggedIn = UserHelper.isUserLoggedIn(this);

        if (isAppConfigured) {
           /* The app has been set up! Go ahead and display stuff.
            *
            */
            if (isLoggedIn) {
                switchToModeForPosition(0);
            } else {
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

        drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        navigationDrawerList = (ListView) findViewById(R.id.navigation_drawer_list);
        setUpNavigationDrawer();
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

    private void onItemSelected(int position) {
        switchToModeForPosition(position);
        drawerLayout.closeDrawers();
    }

    private void switchToModeForPosition(int position) {
        if (currentPosition == position) {
            return;
        }
        switch (position) {
            case 0:
                //Match scouting
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MatchScoutingMainFragment()).commit();
                getSupportActionBar().setTitle(MODE_NAMES[0]);
                break;
            case 1:
                // Pit scouting
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PitScoutingMainFragment()).commit();
                getSupportActionBar().setTitle(MODE_NAMES[1]);
            case 2:
                // Notes
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotesMainFragment()).commit();
                getSupportActionBar().setTitle(MODE_NAMES[2]);
            default:
                break;
        }
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
        } else if (id == R.id.action_log_out) {
            UserHelper.logOutAllUsers(this);
            startActivity(new Intent(this, UserLoginActivity.class));
            this.finish();
        } else if (id == R.id.action_add_user) {
            Intent i = new Intent(this, UserLoginActivity.class);
            i.putExtra(UserLoginActivity.EXTRA_CREATE_NEW_HOME, false);
            startActivity(i);
        } else if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
