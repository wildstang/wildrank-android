package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.activities.ScoutPitActivity;

public class PitScoutingFragment extends ScoutingFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scout_pit, container, false);
        view.findViewById(R.id.finish).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.finish) {
            ((ScoutPitActivity) getActivity()).finishScouting();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ScoutingNoteFragment f = ScoutingNoteFragment.newInstance(((ScoutPitActivity) getActivity()).teamKey);
        getFragmentManager().beginTransaction().replace(R.id.notes_container, f, "notes").commit();
    }
}
