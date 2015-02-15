package org.wildstang.wildrank.androidv2.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

import org.wildstang.wildrank.androidv2.R;

/**
 * Created by Nathan on 1/31/2015.
 */
public class ManagerUsersActivity extends ActionBarActivity implements View.OnClickListener {

    ListView usersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_users);

        usersList = (ListView) findViewById(R.id.users_list);

        findViewById(R.id.add_user_button).setOnClickListener(this);
        findViewById(R.id.logout_all_users_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_user_button) {

        } else if (id == R.id.logout_all_users_button) {

        }

    }
}
