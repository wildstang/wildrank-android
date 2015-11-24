package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.CouchbaseLiteException;

import org.wildstang.wildrank.androidv2.NoteBox;
import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.io.IOException;

public class TeamNotesFragment extends Fragment {
    public NoteBox box;
    String team;

    public static TeamNotesFragment newInstance(String team) {
        TeamNotesFragment f = new TeamNotesFragment();
        Bundle b = new Bundle();
        b.putString("team", team);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            team = getArguments().getString("team");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_notes, container, false);
        box = new NoteBox(view.findViewById(R.id.one), Integer.toString(Utilities.teamNumberFromTeamKey(team)));
        try {
            box.setOldNotes(DatabaseManager.getInstance(getActivity()).getNotes(team), getActivity());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    public String getNote() {
        String note = "";
        note = box.getNote();
        return note;
    }
}