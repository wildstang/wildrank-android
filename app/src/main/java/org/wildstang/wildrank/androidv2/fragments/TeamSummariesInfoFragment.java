package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.views.TemplatedTextView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesInfoFragment extends TeamSummariesFragment {

    private int teamNumber;
    private String teamName;
    private String teamKey;

    private TextView teamNameView;
    private TextView teamNumberView;
    private TextView notesView;
    private TextView pitsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summaries_info, container, false);
        teamNumberView = (TextView) v.findViewById(R.id.team_number);
        teamNameView = (TextView) v.findViewById(R.id.team_name);
        v.findViewById(R.id.content).setVisibility(View.GONE);
        return v;
    }

    @Override
    public void acceptNewTeamData(String teamKey, Document teamDoc, Document pitDoc, List<Document> matchDocs) {
        this.teamKey = teamKey;
        this.teamNumber = Integer.parseInt(Utilities.teamNumberFromTeamKey(teamKey));

        getView().findViewById(R.id.select_a_team).setVisibility(View.GONE);
        getView().findViewById(R.id.content).setVisibility(View.VISIBLE);

        try {
            System.out.println("team doc null? " + teamDoc == null);
            teamName = (String) teamDoc.getProperty("nickname");
            teamNumberView.setText("Team " + teamNumber);
            teamNameView.setText(teamName);

            notesView = (TextView) getView().findViewById(R.id.notes);
            notesView.setText("");

            String[] notes = DatabaseManager.getInstance(getActivity()).getNotes(teamKey);
            for (int i = 0; i < notes.length; i++) {
                notesView.append("-" + notes[i] + "\n");
            }

            if (pitDoc != null) {
                Log.d("wildrank", pitDoc.getProperties().toString());
                TemplatedTextView.initializeViewsInViewGroupWithMap((ViewGroup) getView(), (Map<String, Object>) pitDoc.getProperty("data"));
            } else {
                TemplatedTextView.clearAllViewsInViewGroup((ViewGroup) getView());
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}