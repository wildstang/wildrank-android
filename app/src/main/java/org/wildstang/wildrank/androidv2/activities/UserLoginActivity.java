package org.wildstang.wildrank.androidv2.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.UserHelper;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

/**
 * Created by Nathan on 1/27/2015.
 */
public class UserLoginActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String EXTRA_CREATE_NEW_HOME = "create_new_home";

    private boolean createNewHome = true;

    EditText userLoginEditText;
    TextView userWelcomeMessage;
    View loginContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        findViewById(R.id.login_button).setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if (extras.containsKey(EXTRA_CREATE_NEW_HOME)) {
                createNewHome = extras.getBoolean(EXTRA_CREATE_NEW_HOME, true);
            }
        }

        userLoginEditText = (EditText) findViewById(R.id.login_user_id);
        userWelcomeMessage = (TextView) findViewById(R.id.user_welcome_message);
        loginContainer = findViewById(R.id.login_container);
    }

    private void doLogin() {
        try {
            String userID = userLoginEditText.getText().toString();
            Document user = DatabaseManager.getInstance(this).getUserById(userID);
            if (user != null) {
                // Do login
                String userName = (String) user.getProperty("name");
                UserHelper.logInUser(this, userID);
                String[] users = UserHelper.getLoggedInUsersAsArray(this);
                for(String userNumber : users) {
                    Log.d("wildrank", "Logged in user: " + userNumber);
                }
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
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void doFancyAnimationsAndFinish(String userName) {
        userWelcomeMessage.setText("Welcome, " + userName);


        ValueAnimator fadeLoginOut = ValueAnimator.ofFloat(1, 0);
        fadeLoginOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("wildrank", "alpha updated! " + animation.getAnimatedValue());
                loginContainer.setAlpha((float) animation.getAnimatedValue());
            }
        });
        fadeLoginOut.setDuration(400);

        ValueAnimator fadeWelcomeIn = ValueAnimator.ofFloat(0, 1);
        fadeWelcomeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                userWelcomeMessage.setVisibility(View.VISIBLE);
            }
        });
        fadeWelcomeIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                userWelcomeMessage.setAlpha((float) animation.getAnimatedValue());
            }
        });
        fadeWelcomeIn.setDuration(400);

        ValueAnimator fadeWelcomeOut = ValueAnimator.ofFloat(1, 0);
        fadeWelcomeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                userWelcomeMessage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(createNewHome) {
                    startActivity(new Intent(UserLoginActivity.this, HomeActivity.class));
                }
                UserLoginActivity.this.finish();
            }
        });
        fadeWelcomeOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                userWelcomeMessage.setAlpha((float) animation.getAnimatedValue());
            }
        });
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
            try {
                DatabaseManager.getInstance(this).dumpDatabaseContentsToLog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
