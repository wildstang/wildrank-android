package org.wildstang.wildrank.androidv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * Created by Nathan on 1/27/2015.
 */
public class UserHelper {

    public static final String LOGGED_IN_USER_PREFERENCE_KEY = "logged_in_user";

    public static boolean isUserLoggedIn(Context context) {
        return !PreferenceManager.getDefaultSharedPreferences(context).getStringSet(LOGGED_IN_USER_PREFERENCE_KEY, new HashSet<String>()).isEmpty();
    }

    public static void logInUser(Context context, String userId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> currentUsers = prefs.getStringSet(LOGGED_IN_USER_PREFERENCE_KEY, new HashSet<String>());
        currentUsers.add(userId);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(LOGGED_IN_USER_PREFERENCE_KEY, currentUsers).commit();
    }

    public static void logOutAllUsers(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(LOGGED_IN_USER_PREFERENCE_KEY).commit();
    }

    public static Set<String> getLoggedInUsers(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(LOGGED_IN_USER_PREFERENCE_KEY, new HashSet<String>());
    }

    public static String[] getLoggedInUsersAsArray(Context context) {
        return getLoggedInUsers(context).toArray(new String[0]);
    }
}
