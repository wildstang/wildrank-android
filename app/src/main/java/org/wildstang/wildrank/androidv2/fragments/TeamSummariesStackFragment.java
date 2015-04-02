package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.views.data.StackDataView;

import java.util.List;

/**
 * Created by Liam on 3/20/2015.
 */
public class TeamSummariesStackFragment extends TeamSummariesFragment {
    StackDataView view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stack, container, false);
        view = (StackDataView) v.findViewById(R.id.stackview);
        return v;
    }

    @Override
    public void acceptNewTeamData(String teamKey, Document teamDoc, Document pitDoc, List<Document> matchDocs) {
        view.acceptNewTeamData(matchDocs);
    }
}
