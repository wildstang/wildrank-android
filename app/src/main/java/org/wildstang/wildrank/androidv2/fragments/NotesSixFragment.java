package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.wildstang.wildrank.androidv2.NoteBox;
import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.activities.ScoutMatchActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liam on 2/21/2015.
 */
public class NotesSixFragment extends Fragment
{
    public List<NoteBox> boxes = new ArrayList<>();
    String[] teams;

    public NotesSixFragment(String[] teams)
    {
        this.teams = teams;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notes_six, container, false);
        boxes.add(new NoteBox(view.findViewById(R.id.one), teams[0]));
        boxes.add(new NoteBox(view.findViewById(R.id.two), teams[1]));
        boxes.add(new NoteBox(view.findViewById(R.id.three), teams[2]));
        boxes.add(new NoteBox(view.findViewById(R.id.four), teams[3]));
        boxes.add(new NoteBox(view.findViewById(R.id.five), teams[4]));
        boxes.add(new NoteBox(view.findViewById(R.id.six), teams[5]));
        return view;
    }
}