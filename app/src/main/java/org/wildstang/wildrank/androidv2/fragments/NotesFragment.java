package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wildstang.wildrank.androidv2.R;

/**
 * Created by Liam on 2/21/2015.
 */
public class NotesFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        return view;
    }
}