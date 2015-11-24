package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.couchbase.lite.CouchbaseLiteException;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.io.IOException;

public class ScoutingNoteFragment extends Fragment implements View.OnClickListener {

    private EditText notes;

    private String teamKey;

    public static ScoutingNoteFragment newInstance(String teamkey) {
        ScoutingNoteFragment f = new ScoutingNoteFragment();
        Bundle b = new Bundle();
        b.putString("teamkey", teamkey);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get details about the team we are scouting from the intent
        if (getActivity() != null) {
            teamKey = getArguments().getString("teamkey");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveNotes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scouting_note, container, false);

        // Save references to our important views
        notes = (EditText) v.findViewById(R.id.notes);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveNotes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveNotes() {
        // Only save notes when the text field is not empty
        try {
            DatabaseManager.getInstance(getActivity()).saveNotes(teamKey, notes.getText().toString(), getActivity());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finish) {
            saveNotes();
        }
    }
}