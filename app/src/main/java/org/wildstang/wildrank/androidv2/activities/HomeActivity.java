package org.wildstang.wildrank.androidv2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.activities.AppSetupActivity;
import org.wildstang.wildrank.androidv2.fragments.MatchScoutingMainFragment;


public class HomeActivity extends ActionBarActivity {

    public static final String PREF_IS_APP_CONFIGURED = "is_app_configured";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

        boolean isAppConfigured = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_IS_APP_CONFIGURED, false);

        if (isAppConfigured) {
           /* The app has been set up! Go ahead and display stuff.
            *
            */
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MatchScoutingMainFragment()).commit();
        } else {
            /* The app hasn't been setup yet. Launch the setup activity and finish this one.
             * After setup is finished, we will relaunch this one.
             */
            startActivity(new Intent(this, AppSetupActivity.class));
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
