package org.wildstang.wildrank.androidv2.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.activities.ScoutMatchActivity;
import org.wildstang.wildrank.androidv2.adapters.MatchListAdapter;
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QueryRow row = (QueryRow) parent.getItemAtPosition(position);
                onTeamSelected(row.getDocument());
            }
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
        adapter = new TeamListAdapter(getActivity(), queryRows);
        list.setAdapter(adapter);
        list.onRestoreInstanceState(state);
    }

    private void onTeamSelected(Document matchDocument) {
        selectedTeamKey = (String) matchDocument.getProperty("team_key");
        Log.d("wildrank", "match key is null? " + (selectedTeamKey == null));

        int teamNumber = (Integer) matchDocument.getProperty("team_number");
        this.teamNumber.setText("Match " + teamNumber);

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
        if (id == R.id.begin_scouting) {/*
            // Launch the scouting activity
            String allianceColor;
            String assignedTeamType = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("assignedTeams", "red_1");
            if (assignedTeamType.contains("red")) {
                allianceColor = ScoutMatchActivity.EXTRA_ALLIANCE_COLOR_RED;
            } else {
                allianceColor = ScoutMatchActivity.EXTRA_ALLIANCE_COLOR_BLUE;
            }
            final Intent intent = ScoutMatchActivity.createIntent(getActivity(), selectedMatchKey, selectedTeamToScout, allianceColor);
            boolean isMatchScouted;
            try {
                isMatchScouted = DatabaseManager.getInstance(getActivity()).isMatchScouted(selectedMatchKey, selectedTeamToScout);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error determining if match is scouted already.", Toast.LENGTH_SHORT).show();
                isMatchScouted = false;
            }
            if (isMatchScouted) {
                // Prompt to overwrite data
                new AlertDialog.Builder(getActivity())
                        .setTitle("Rescouting match")
                        .setMessage("Existing match data will be overwritten if you continue.")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                // Begin scouting as normal!
                startActivity(intent);

            }*/
        }
    }
}
