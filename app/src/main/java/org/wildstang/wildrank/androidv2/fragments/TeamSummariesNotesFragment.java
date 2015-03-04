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

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesNotesFragment extends Fragment
{
    private int teamNumber;
    private String teamName;
    private String teamKey;

    private TextView teamNumberView;
    private TextView teamNameView;
    private TextView notesView;
    private TextView pitsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_summaries_notes, container, false);
        teamKey = getArguments().getString("team_key");
        teamNumber = Integer.parseInt(Utilities.teamNumberFromTeamKey(teamKey));
        Document teamDoc;
        try
        {
            teamDoc = DatabaseManager.getInstance(getActivity()).getDatabase().getExistingDocument("team:" + teamKey);
            teamName = (String) teamDoc.getProperty("nickname");
            teamNumberView = (TextView) getView().findViewById(R.id.team_number);
            teamNumberView.setText("" + teamNumber);
            teamNameView = (TextView) getView().findViewById(R.id.team_name);
            teamNameView.setText(teamName);

            notesView = (TextView) getView().findViewById(R.id.notes);
            pitsView = (TextView) getView().findViewById(R.id.pit);

            pitsView.setText("pits shits");

            String[] notes = DatabaseManager.getInstance(getActivity()).getNotes(teamKey);
            notesView.setText("");
            for(int i = 0; i < notes.length; i++)
            {
                notesView.append("*" + notes[i] + "\n");
            }
        }
        catch (CouchbaseLiteException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return v;
    }
}