package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.couchbase.lite.Document;

/**
 * Created by Liam on 2/28/2015.
 */
public abstract class TeamSummariesFragment extends Fragment {
    Document document;
    String selectedTeamKey = "";
    int teamNumber = 0;

    public void update(Document document) {
        this.document = document;
        selectedTeamKey = (String) document.getProperty("key");
        teamNumber = (Integer) document.getProperty("team_number");
    }

    public abstract void updateTeamKey(String teamKey);
}
