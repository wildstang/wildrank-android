package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.adapters.MatchListAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MatchScoutingMainFragment extends Fragment implements View.OnClickListener {

    private ListView list;
    private TextView matchNumber;
    private TextView scoutingTeam;
    private Button beginScouting;

    private LiveQuery liveQuery;

    private MatchListAdapter adapter;

    public MatchScoutingMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_scouting_main, container, false);
        list = (ListView) view.findViewById(R.id.match_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QueryRow row = (QueryRow) parent.getItemAtPosition(position);
                onMatchSelected(row.getDocument());
            }
        });
        matchNumber = (TextView) view.findViewById(R.id.match_number);
        scoutingTeam = (TextView) view.findViewById(R.id.scouting_team);
        beginScouting = (Button) view.findViewById(R.id.begin_scouting);
        beginScouting.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            setUpListAndBeginLiveQuery();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something went wrong when creating the match list query. Damn.", Toast.LENGTH_LONG).show();
        }
    }

    private void setUpListAndBeginLiveQuery() throws Exception {
        if (liveQuery == null) {
            liveQuery = DatabaseManager.getInstance(getActivity()).getAllMatches();

            adapter = new MatchListAdapter(getActivity(), new ArrayList<QueryRow>());

            liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                @Override
                public void changed(final LiveQuery.ChangeEvent changeEvent) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();
                            for (Iterator<QueryRow> it = changeEvent.getRows(); it.hasNext(); ) {
                                adapter.add(it.next());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });

            liveQuery.start();

            /*List<QueryRow> queryRows = new ArrayList<>();
            for (Iterator<QueryRow> it = liveQuery.getRows(); it.hasNext();) {
                queryRows.add(it.next());
            }
            adapter = new MatchListAdapter(getActivity(), queryRows);
            list.setAdapter(adapter);*/
        }
    }

    private void onMatchSelected(Document matchDocument) {
        int matchNumber = (Integer) matchDocument.getProperty("match_number");
        this.matchNumber.setText("Match " + matchNumber);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.begin_scouting) {
            // Launch the scouting activity
        }
    }
}
