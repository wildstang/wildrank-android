package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.views.StackView;
import org.wildstang.wildrank.androidv2.views.data.MatchDataView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Liam on 3/20/2015.
 */
public class TeamSummariesStackFragment extends TeamSummariesFragment {
    StackView view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stack, container, false);
        view = (StackView) v.findViewById(R.id.stackview);
        return v;
    }

    @Override
    public void updateTeamKey(String teamKey) {
        view.updateData(teamKey);
    }
}
