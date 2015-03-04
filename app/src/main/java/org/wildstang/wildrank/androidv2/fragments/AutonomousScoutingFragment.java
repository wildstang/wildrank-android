package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.activities.ScoutMatchActivity;

/**
 * Created by Nathan on 1/24/2015.
 */
public class AutonomousScoutingFragment extends ScoutingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scout_autonomous, container, false);
        return view;
    }
}
