package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.util.List;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesGraphFragment extends TeamSummariesFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summaries_graph, container, false);
        return view;
    }

    @Override
    public void updateTeamKey(String teamKey) {
        StringBuilder rawDataString = new StringBuilder();

        TextView rawData = ((TextView) getView().findViewById(R.id.raw_data));
        try {
            List<Document> docs = DatabaseManager.getInstance(getActivity()).getMatchResultsForTeam(teamKey);
            if(docs == null) {
                rawData.setText("No match results found.");
                return;
            } else if (docs.size() == 0) {
                rawData.setText("No match results found.");
                return;
            }
            for (Document doc : docs) {
                try {
                    Gson gson = new Gson();
                    String json = gson.toJson(doc.getProperties());
                    JSONObject matchData = new JSONObject(json);
                    rawDataString.append(matchData.toString(4));
                } catch (Exception e) {
                    e.printStackTrace();
                    rawDataString.append("ERROR");
                }
                rawDataString.append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
            rawDataString.append("ERROR");
        }
        rawData.setText(rawDataString.toString());
    }
}
