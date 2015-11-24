package org.wildstang.wildrank.androidv2.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.activities.ScoutPitActivity;
import org.wildstang.wildrank.androidv2.adapters.TeamListAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PitScoutingMainFragment extends Fragment implements View.OnClickListener {

    private ListView list;

    private TextView teamNumber;
    private Button beginScouting;

    private String selectedTeamKey;

    private TeamListAdapter adapter;


    public PitScoutingMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pit_scouting_main, container, false);

        list = (ListView) view.findViewById(R.id.match_list);
        list.setOnItemClickListener((parent, view1, position, id) -> {
            QueryRow row = (QueryRow) parent.getItemAtPosition(position);
            onTeamSelected(row.getDocument());
        });

        // We reuse this view to show the "select a match" message to avoid having another view
        teamNumber = (TextView) view.findViewById(R.id.team_number);
        teamNumber.setText("Please select a team.");

        beginScouting = (Button) view.findViewById(R.id.begin_scouting);
        beginScouting.setOnClickListener(this);
        beginScouting.setEnabled(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            runQuery();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error querying the match list. Check logcat!", Toast.LENGTH_LONG).show();
        }
    }

    private void runQuery() throws Exception {
        Query query = DatabaseManager.getInstance(getActivity()).getAllTeams();

        QueryEnumerator enumerator = query.run();

        Log.d("wildrank", "team query count: " + enumerator.getCount());

        List<QueryRow> queryRows = new ArrayList<>();
        for (Iterator<QueryRow> it = enumerator; it.hasNext(); ) {
            QueryRow row = it.next();
            queryRows.add(row);
            Log.d("wildstang", "Document key: " + row.getKey());
        }

        Parcelable state = list.onSaveInstanceState();
        adapter = new TeamListAdapter(getActivity(), queryRows, true);
        list.setAdapter(adapter);
        list.onRestoreInstanceState(state);
    }

    private void onTeamSelected(Document matchDocument) {
        selectedTeamKey = (String) matchDocument.getProperty("key");
        Log.d("wildrank", "match key is null? " + (selectedTeamKey == null));

        int teamNumber = (Integer) matchDocument.getProperty("team_number");
        this.teamNumber.setText("Team " + teamNumber);

        beginScouting.setEnabled(true);
        try {
            if (DatabaseManager.getInstance(getActivity()).isTeamPitScouted(selectedTeamKey)) {
                beginScouting.setText(R.string.rescout_team);
            } else {
                beginScouting.setText(R.string.being_scouting);
            }
        } catch (Exception e) {
            e.printStackTrace();
            beginScouting.setText(R.string.being_scouting);
        }
        beginScouting.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.begin_scouting) {
            // Launch the scouting activity
            final Intent intent = ScoutPitActivity.createIntent(getActivity(), selectedTeamKey);
            boolean isTeamScouted;
            try {
                isTeamScouted = DatabaseManager.getInstance(getActivity()).isTeamPitScouted(selectedTeamKey);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error determining if team is scouted already.", Toast.LENGTH_SHORT).show();
                isTeamScouted = false;
            }
            if (isTeamScouted) {
                // Prompt to overwrite data
                new AlertDialog.Builder(getActivity())
                        .setTitle("Rescouting team")
                        .setMessage("Existing team data will be overwritten if you continue.")
                        .setPositiveButton("Continue", (dialog, which) -> startActivity(intent))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
            } else {
                // Begin scouting as normal!
                startActivity(intent);

            }
        }
    }
}
