package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.views.data.MatchDataView;

import java.util.List;

public class TeamSummariesDataFragment extends TeamSummariesFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summaries_data, container, false);
    }

    @Override
    public void acceptNewTeamData(String teamKey, Document teamDoc, Document pitDoc, List<Document> matchDocs) {
        MatchDataView.clearAllViewsInViewGroup((ViewGroup) getView());
        MatchDataView.initializeViewsInViewGroupWithDocuments((ViewGroup) getView(), matchDocs);
    }
}