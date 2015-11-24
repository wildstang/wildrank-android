package org.wildstang.wildrank.androidv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.models.UserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static void logOutUser(Context context, String userId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> currentUsers = prefs.getStringSet(LOGGED_IN_USER_PREFERENCE_KEY, new HashSet<>());
        currentUsers.remove(userId);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(LOGGED_IN_USER_PREFERENCE_KEY, currentUsers).commit();
    }

    public static Set<String> getLoggedInUsers(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(LOGGED_IN_USER_PREFERENCE_KEY, new HashSet<>());
    }

    public static List<String> getLoggedInUsersAsList(Context context) {
        Set<String> users = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(LOGGED_IN_USER_PREFERENCE_KEY, new HashSet<>());
        return new ArrayList<String>(users);
    }

    public static List<UserModel> getLoggedInUserModelsAsList(Context context) {
        Set<String> usersSet = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(LOGGED_IN_USER_PREFERENCE_KEY, new HashSet<>());
        List<String> userIds = new ArrayList<>(usersSet);
        List<UserModel> users = new ArrayList<>();
        for (String id : userIds) {
            UserModel user = new UserModel();
            user.userId = id;
            user.userName = getUserNameForId(context, id);
            users.add(user);
        }
        return users;
    }

    public static String getUserNameForId(Context c, String id) {
        try {
            Document user = DatabaseManager.getInstance(c).getUserById(id);
            if (user != null) {
                return (String) user.getProperty("name");
            } else {
                return "";
            }
        } catch (CouchbaseLiteException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String[] getLoggedInUsersAsArray(Context context) {
        return getLoggedInUsers(context).toArray(new String[0]);
    }
}
