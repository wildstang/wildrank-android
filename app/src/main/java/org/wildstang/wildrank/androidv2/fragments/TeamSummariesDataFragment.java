package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wildstang.wildrank.androidv2.R;

import java.util.ArrayList;

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

    }
}