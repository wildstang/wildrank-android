package org.wildstang.wildrank.androidv2;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Nathan on 1/20/2015.
 */
public class Utilities {

    public static String getExternalRootDirectory() {
        return "/storage/usbdisk0/WildRank/cblite";
    }

    public static Object[] getRedTeamsFromMatchDocument(Document matchDocument) {
        Map<String, Object> properties = matchDocument.getProperties();
        Map<String, Object> alliances = (Map<String, Object>) properties.get("alliances");
        Map<String, Object> redAlliance = (Map<String, Object>) alliances.get("red");
        return  ((ArrayList<Object>) redAlliance.get("teams")).toArray();
    }

    public static Object[] getBlueTeamsFromMatchDocument(Document matchDocument) {
        Map<String, Object> properties = matchDocument.getProperties();
        Map<String, Object> alliances = (Map<String, Object>) properties.get("alliances");
        Map<String, Object> blueAlliance = (Map<String, Object>) alliances.get("blue");
        return ((ArrayList<Object>) blueAlliance.get("teams")).toArray();
    }

    public static int matchNumberFromMatchKey(String matchKey) {
        return Integer.parseInt(matchKey.substring(matchKey.lastIndexOf('m') + 1));
    }

    public static String teamNumberFromTeamKey(String teamKey) {
        return teamKey.replace("frc","");
    }

    public static String getAssignedTeamKeyFromMatchDocument(Context context, Document matchDocument) {
        String assignedTeamType = PreferenceManager.getDefaultSharedPreferences(context).getString("assignedTeam", "red_1");
        switch (assignedTeamType) {
            case "red_1":
                return Utilities.getRedTeamsFromMatchDocument(matchDocument)[0].toString();
            case "red_2":
                return Utilities.getRedTeamsFromMatchDocument(matchDocument)[1].toString();
            case "red_3":
                return Utilities.getRedTeamsFromMatchDocument(matchDocument)[2].toString();
            case "blue_1":
                return Utilities.getBlueTeamsFromMatchDocument(matchDocument)[0].toString();
            case "blue_2":
                return Utilities.getBlueTeamsFromMatchDocument(matchDocument)[1].toString();
            case "blue_3":
                return Utilities.getBlueTeamsFromMatchDocument(matchDocument)[2].toString();
            default:
                return "";
        }
    }
}
