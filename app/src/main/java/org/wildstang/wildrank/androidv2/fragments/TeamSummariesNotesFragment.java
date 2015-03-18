package org.wildstang.wildrank.androidv2.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesNotesFragment extends TeamSummariesFragment {

    private static final String ARGUMENT_TEAM_KEY = "TEAM_KEY";

    private int teamNumber;
    private String teamName;
    private String teamKey;

    private TextView teamView;
    private TextView notesView;
    private TextView pitsView;

    public static TeamSummariesNotesFragment newInstance(String teamKey) {
        TeamSummariesNotesFragment fragment = new TeamSummariesNotesFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_TEAM_KEY, teamKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summaries_notes, container, false);
        teamView = (TextView) v.findViewById(R.id.team);
        return v;
    }

    @Override
    public void updateTeamKey(String teamKey) {
        this.teamKey = teamKey;
        this.teamNumber = Integer.parseInt(Utilities.teamNumberFromTeamKey(teamKey));

        try {
            Document pitDoc = DatabaseManager.getInstance(getActivity()).getDatabase().getExistingDocument("pit:" + teamKey);
            Document teamDoc = DatabaseManager.getInstance(getActivity()).getDatabase().getExistingDocument("team:" + teamKey);
            System.out.println(teamDoc != null);
            teamName = (String) teamDoc.getProperty("nickname");
            teamView.setText(teamNumber + " - " + teamName);

            notesView = (TextView) getView().findViewById(R.id.notes);
            pitsView = (TextView) getView().findViewById(R.id.pit);

            pitsView.setText("Pit Data");

            String[] notes = DatabaseManager.getInstance(getActivity()).getNotes(teamKey);
            notesView.setText("Notes\n");
            for (int i = 0; i < notes.length; i++) {
                notesView.append("-" + notes[i] + "\n");
            }

            if(pitDoc != null)
            {
                Map<String, Object> pitData = (Map<String, Object>) pitDoc.getProperties().get("data");
                Iterator<Map.Entry<String, Object>> iterator = pitData.entrySet().iterator();
                for(int i = 0; i < pitData.size(); i++)
                {
                    Map.Entry<String, Object> dataPoint = iterator.next();
                    pitsView.append("\n-" + dataPoint.getKey().replace("pit-","").replace("_", " ") + ": " + dataPoint.getValue());
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}