package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wildstang.wildrank.androidv2.NoteBox;
import org.wildstang.wildrank.androidv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liam on 2/21/2015.
 */
public class NotesFragment extends Fragment
{
    public NoteBox box;
    String team;

    public NotesFragment(String team)
    {
        this.team = team;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        box = new NoteBox(view.findViewById(R.id.one), team);
        return view;
    }
}