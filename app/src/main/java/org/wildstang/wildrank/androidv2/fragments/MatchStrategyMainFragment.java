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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.activities.MatchStrategyActivity;
import org.wildstang.wildrank.androidv2.activities.ScoutMatchActivity;
import org.wildstang.wildrank.androidv2.adapters.MatchListAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Janine on 3/13/2016.
 */
public class MatchStrategyMainFragment extends Fragment implements View.OnClickListener {
    private ListView list;

    private Button enterStrategy;
    private TextView matchNumber;
    private String[] selectedTeams;
    private String selectedMatchKey;

    private MatchListAdapter adapter;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    public MatchStrategyMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (sharedPreferences, key) -> {
            if (key == null) {
                return;
            }
            if (key.equals("assignedTeam")) {
                if (MatchStrategyMainFragment.this.isAdded()) {
                    // Requery the list to update which matches are scouted or not
                    try {
                        Log.d("wildrank", "Requerying match list!");

                        runQuery();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error querying the match list. Check logcat!", Toast.LENGTH_LONG).show();
                    }

                    // Update the selected match view if it exists
                    if (selectedMatchKey != null) {
                        try {
                            Log.d("wildrank", "Requerying match details!");
                            onMatchSelected(DatabaseManager.getInstance(MatchStrategyMainFragment.this.getActivity()).getMatchFromKey(selectedMatchKey));
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
        View view = inflater.inflate(R.layout.fragment_match_strategy_main, container, false);

        list = (ListView) view.findViewById(R.id.match_list);
        list.setOnItemClickListener((parent, view1, position, id) -> {
            QueryRow row = (QueryRow) parent.getItemAtPosition(position);
            onMatchSelected(row.getDocument());
        });

        // We reuse this view to show the "select a match" message to avoid having another view
        matchNumber = (TextView) view.findViewById(R.id.match_number);
        matchNumber.setText("Please select a match.");

        enterStrategy = (Button) view.findViewById(R.id.enter_strategy);
        enterStrategy.setOnClickListener(this);
        enterStrategy.setEnabled(false);
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
        Query query = DatabaseManager.getInstance(getActivity()).getAllMatches();

        adapter = new MatchListAdapter(getActivity(), new ArrayList<QueryRow>());

        QueryEnumerator enumerator = query.run();

        List<QueryRow> queryRows = new ArrayList<>();
        for (Iterator<QueryRow> it = enumerator; it.hasNext(); ) {
            QueryRow row = it.next();
            queryRows.add(row);
        }

        Parcelable state = list.onSaveInstanceState();
        adapter = new MatchListAdapter(getActivity(), queryRows);
        list.setAdapter(adapter);
        list.onRestoreInstanceState(state);
    }

    private void onMatchSelected(Document matchDocument) {
        selectedMatchKey = (String) matchDocument.getProperty("key");

        int matchNumber = (Integer) matchDocument.getProperty("match_number");
        this.matchNumber.setText("Match " + matchNumber);

        Object[] objects = Utilities.getTeamsFromMatchDocument(matchDocument);
        selectedTeams = Arrays.copyOf(objects, objects.length, String[].class);

        enterStrategy.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.enter_strategy) {
            // Launch the scouting activity
            String allianceColor;
            allianceColor = ScoutMatchActivity.EXTRA_ALLIANCE_COLOR_RED;
            final Intent intent = MatchStrategyActivity.createIntent(getActivity(), selectedMatchKey, selectedTeams);

            startActivity(intent);
        }
    }
}