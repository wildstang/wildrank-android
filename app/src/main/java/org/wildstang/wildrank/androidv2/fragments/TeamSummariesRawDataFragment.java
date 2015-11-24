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

import java.util.List;

public class TeamSummariesRawDataFragment extends TeamSummariesFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summaries_graph, container, false);
        return view;
    }

    @Override
    public void acceptNewTeamData(String teamKey, Document teamDoc, Document pitDoc, List<Document> matchDocs) {
        StringBuilder rawDataString = new StringBuilder();

        TextView rawData = ((TextView) getView().findViewById(R.id.raw_data));
        try {
            if (matchDocs == null) {
                rawData.setText("No match results found.");
                return;
            } else if (matchDocs.size() == 0) {
                rawData.setText("No match results found.");
                return;
            }
            for (Document doc : matchDocs) {
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
