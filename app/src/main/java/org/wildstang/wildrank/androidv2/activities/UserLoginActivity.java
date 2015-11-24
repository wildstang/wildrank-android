package org.wildstang.wildrank.androidv2.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.BuildConfig;
import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.UserHelper;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

public class UserLoginActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    public static final String EXTRA_CREATE_NEW_HOME = "create_new_home";
    EditText userLoginEditText;
    TextView userWelcomeMessage;
    View loginContainer;
    Button sync;
    private boolean createNewHome = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        findViewById(R.id.login_button).setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_CREATE_NEW_HOME)) {
                createNewHome = extras.getBoolean(EXTRA_CREATE_NEW_HOME, true);
            }
        }

        userLoginEditText = (EditText) findViewById(R.id.login_user_id);
        userWelcomeMessage = (TextView) findViewById(R.id.user_welcome_message);
        loginContainer = findViewById(R.id.login_container);
        sync = (Button) findViewById(R.id.sync);
        sync.setOnClickListener(this);
        ((TextView) findViewById(R.id.version)).setText(BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");

        userLoginEditText.setOnEditorActionListener(this);
        updateAssignedTeam();
    }

    private void doLogin() {
        try {
            String userID = userLoginEditText.getText().toString();
            Document user = DatabaseManager.getInstance(this).getUserById(userID);
            if (user != null) {
                // Do login
                String userName = (String) user.getProperty("name");
                UserHelper.logInUser(this, userID);
                doFancyAnimationsAndFinish(userName);
            } else {
                // User not found!
                warnUserNotFound();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error logging in user!", Toast.LENGTH_SHORT).show();
        }
    }

    private void warnUserNotFound() {
        new AlertDialog.Builder(this)
                .setTitle("User not found")
                .setMessage("Please check that your ID is correct!")
                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void doFancyAnimationsAndFinish(String userName) {
        userWelcomeMessage.setText("Welcome, " + userName);


        ValueAnimator fadeLoginOut = ValueAnimator.ofFloat(1, 0);
        fadeLoginOut.addUpdateListener(animation -> {
            loginContainer.setAlpha((float) animation.getAnimatedValue());
        });
        fadeLoginOut.setDuration(400);

        ValueAnimator fadeWelcomeIn = ValueAnimator.ofFloat(0, 1);
        fadeWelcomeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                userWelcomeMessage.setVisibility(View.VISIBLE);
            }
        });
        fadeWelcomeIn.addUpdateListener(animation -> userWelcomeMessage.setAlpha((float) animation.getAnimatedValue()));
        fadeWelcomeIn.setDuration(400);

        ValueAnimator fadeWelcomeOut = ValueAnimator.ofFloat(1, 0);
        fadeWelcomeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                userWelcomeMessage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (createNewHome) {
                    startActivity(new Intent(UserLoginActivity.this, HomeActivity.class));
                }
                UserLoginActivity.this.finish();
            }
        });
        fadeWelcomeOut.addUpdateListener(animation -> userWelcomeMessage.setAlpha((float) animation.getAnimatedValue()));
        fadeWelcomeOut.setStartDelay(1000);
        fadeWelcomeOut.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.play(fadeLoginOut);
        set.play(fadeWelcomeIn).after(fadeLoginOut);
        set.play(fadeWelcomeOut).after(fadeWelcomeIn);
        set.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login_button) {
            doLogin();
        } else if (v == sync) {
            startActivity(new Intent(this, SyncActivity.class));
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event == null || event.getAction() == KeyEvent.ACTION_UP) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            doLogin();
            return true;
        }
        return false;
    }

    public void updateAssignedTeam() {
        String assignedTeamType = PreferenceManager.getDefaultSharedPreferences(this).getString("assignedTeam", "red_1");
        ((TextView) findViewById(R.id.assigned_team)).setText(assignedTeamType);
    }
}
