package org.wildstang.wildrank.androidv2.fragments;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.activities.MatchNotesActivity;
import org.wildstang.wildrank.androidv2.activities.TeamNotesActivity;
import org.wildstang.wildrank.androidv2.adapters.MatchListAdapter;
import org.wildstang.wildrank.androidv2.adapters.TeamListAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NotesMainFragment extends Fragment implements View.OnClickListener {

    private ListView list;

    private TextView scoutingTeam;
    private Button beginScouting;
    private TextView matchNumber;
    private CheckBox useNumbers;

    private String[] selectedTeams;
    private String selectedTeamsString;
    private String selectedMatchKey;

    private String selectedTeamKey;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private boolean useTeamNumbers = false;

    public NotesMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (sharedPreferences, key) -> {
            if (key.equals("assignedTeam")) {
                if (NotesMainFragment.this.isAdded()) {
                    // Requery the list to update which matches are scouted or not
                    try {
                        Log.d("wildrank", "Requerying match list!");

                        runQuery();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error querying the match list. Check logcat!", Toast.LENGTH_LONG).show();
                    }

                    // Update the selected match view if it exists
                    if (selectedMatchKey != null || selectedTeamKey != null) {
                        try {
                            Log.d("wildrank", "Requerying match details!");
                            if (useTeamNumbers) {
                                onTeamSelected(DatabaseManager.getInstance(NotesMainFragment.this.getActivity()).getTeamFromKey(selectedTeamKey));
                            } else {
                                onMatchSelected(DatabaseManager.getInstance(NotesMainFragment.this.getActivity()).getMatchFromKey(selectedMatchKey));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("wildrank", "You idiot, it's null!");

                    }
                } else {
                    Log.d("wildrank", "Fragment not added!");
                }
            }

        };

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes_main, container, false);

        list = (ListView) view.findViewById(R.id.match_list);
        list.setOnItemClickListener((parent, view1, position, id) -> {
            QueryRow row = (QueryRow) parent.getItemAtPosition(position);
            if (useTeamNumbers) {
                onTeamSelected(row.getDocument());
            } else {
                onMatchSelected(row.getDocument());
            }
        });

        // We reuse this view to show the "select a match" message to avoid having another view
        matchNumber = (TextView) view.findViewById(R.id.match_number);
        matchNumber.setText("Please select a match.");

        scoutingTeam = (TextView) view.findViewById(R.id.scouting_teams);
        scoutingTeam.setText("");

        useNumbers = (CheckBox) view.findViewById(R.id.useTeams);
        useNumbers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            useTeamNumbers = useNumbers.isChecked();
            try {
                runQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        beginScouting = (Button) view.findViewById(R.id.begin_scouting);
        beginScouting.setOnClickListener(this);
        beginScouting.setEnabled(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Requery the team list
        try {
            runQuery();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error querying the team list. Check logcat!", Toast.LENGTH_LONG).show();
        }
    }

    private void runQuery() throws Exception {
        if (useTeamNumbers) {
            Query query = DatabaseManager.getInstance(getActivity()).getAllTeams();


            QueryEnumerator enumerator = query.run();

            Log.d("wildrank", "team query count: " + enumerator.getCount());

            List<QueryRow> queryRows = new ArrayList<>();
            for (Iterator<QueryRow> it = enumerator; it.hasNext(); ) {
                QueryRow row = it.next();
                queryRows.add(row);
            }
            Parcelable state = list.onSaveInstanceState();
            TeamListAdapter adapter = new TeamListAdapter(getActivity(), queryRows, false);
            list.setAdapter(adapter);
            list.onRestoreInstanceState(state);
        } else {
            Query query = DatabaseManager.getInstance(getActivity()).getAllMatches();

            QueryEnumerator enumerator = query.run();

            Log.d("wildrank", "match query count: " + enumerator.getCount());

            List<QueryRow> queryRows = new ArrayList<>();
            for (Iterator<QueryRow> it = enumerator; it.hasNext(); ) {
                QueryRow row = it.next();
                queryRows.add(row);
                Log.d("wildstang", "Document key: " + row.getKey());
            }
            Parcelable state = list.onSaveInstanceState();
            MatchListAdapter adapter = new MatchListAdapter(getActivity(), queryRows);
            list.setAdapter(adapter);
            list.onRestoreInstanceState(state);
        }
    }

    private void onMatchSelected(Document matchDocument) {
        selectedMatchKey = (String) matchDocument.getProperty("key");
        Log.d("wildrank", "match key is null? " + (selectedMatchKey == null));

        int matchNumber = (Integer) matchDocument.getProperty("match_number");
        this.matchNumber.setText("Match " + matchNumber);

        Object[] objects = Utilities.getTeamsFromMatchDocument(matchDocument);
        selectedTeams = Arrays.copyOf(objects, objects.length, String[].class);

        StringBuilder teams = new StringBuilder();
        for (int i = 0; i < selectedTeams.length; i++) {
            String newTeam = selectedTeams[i].replace("frc", "");
            if (i != selectedTeams.length - 1) {
                teams.append(newTeam + ", ");
            } else {
                teams.append(newTeam);
            }
        }
        selectedTeamsString = teams.toString();
        scoutingTeam.setText(selectedTeamsString);

        beginScouting.setEnabled(true);
    }

    private void onTeamSelected(Document teamDocument) {
        selectedTeamKey = (String) teamDocument.getProperty("key");
        Log.d("wildrank", "team key is null? " + (selectedMatchKey == null));
        matchNumber.setText("Team: " + Utilities.teamNumberFromTeamKey(selectedTeamKey));
        scoutingTeam.setText("");

        beginScouting.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.begin_scouting) {
            // Launch the scouting activity
            if (useTeamNumbers) {
                System.out.println("opening note activity");
                final Intent intent = TeamNotesActivity.createIntent(getActivity(), selectedTeamKey);
                startActivity(intent);
            } else {
                System.out.println("opening noteS activity");
                final Intent intent = MatchNotesActivity.createIntent(getActivity(), selectedMatchKey, selectedTeams);
                startActivity(intent);
            }
        }
    }
}
