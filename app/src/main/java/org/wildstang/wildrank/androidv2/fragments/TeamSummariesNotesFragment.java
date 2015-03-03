package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wildstang.wildrank.androidv2.R;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesNotesFragment extends TeamSummariesFragment
{
    TextView teamNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_summaries_notes, container, false);
        teamNum = (TextView) view.findViewById(R.id.team_number);
        teamNum.setText(Integer.toString(teamNumber));
        return view;
    }
}
