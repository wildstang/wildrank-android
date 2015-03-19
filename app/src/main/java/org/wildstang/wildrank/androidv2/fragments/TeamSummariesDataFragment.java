package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.views.data.MatchDataView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesDataFragment extends TeamSummariesFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summaries_data, container, false);
    }

    @Override
    public void updateTeamKey(String teamKey) {
        MatchDataView.clearAllViewsInViewGroup((ViewGroup) getView());
        try {
            List<Document> docs = DatabaseManager.getInstance(getActivity()).getMatchResultsForTeam(teamKey);
            MatchDataView.initializeViewsInViewGroupWithDocuments((ViewGroup) getView(), docs);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}