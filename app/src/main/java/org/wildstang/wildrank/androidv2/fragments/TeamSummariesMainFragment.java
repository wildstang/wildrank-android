package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.adapters.TeamListAdapter;
import org.wildstang.wildrank.androidv2.adapters.TeamSummariesFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.views.SlidingTabs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeamSummariesMainFragment extends Fragment {
    private ListView teamList;
    private ViewPager pager;
    private SlidingTabs tabs;

    private TeamListAdapter listAdapter;
    private TeamSummariesFragmentPagerAdapter pagerAdapter;

    private String selectedTeamKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summaries_main, container, false);
        teamList = (ListView) view.findViewById(R.id.teams_list);
        pager = (ViewPager) view.findViewById(R.id.view_pager);
        tabs = (SlidingTabs) view.findViewById(R.id.tabs);

        pager.setOffscreenPageLimit(10);
        teamList.setOnItemClickListener((parent, view1, position, id) -> {
            teamList.setItemChecked(position, true);
            QueryRow row = (QueryRow) parent.getItemAtPosition(position);
            onTeamSelected(row.getDocument());
        });

        pager.setAdapter(new TeamSummariesFragmentPagerAdapter(getFragmentManager()));
        tabs.setViewPager(pager);

        String team = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("assignedTeam", "red_1");

        if (team.contains("red")) {
            tabs.setBackgroundColor(getResources().getColor(R.color.material_red));
        } else {
            tabs.setBackgroundColor(getResources().getColor(R.color.material_blue));
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            runQuery();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error querying the team list. Check logcat!", Toast.LENGTH_LONG).show();
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
        }

        Parcelable state = teamList.onSaveInstanceState();
        listAdapter = new TeamListAdapter(getActivity(), queryRows, false);
        teamList.setAdapter(listAdapter);
        teamList.onRestoreInstanceState(state);
    }

    private void onTeamSelected(Document doc) {
        loadInfoForTeam((String) doc.getProperty("key"));
    }

    private void loadInfoForTeam(String teamKey) {
        try {
            DatabaseManager db = DatabaseManager.getInstance(getActivity());
            Document teamDocument = db.getTeamFromKey(teamKey);
            Document pitDocument = db.getInternalDatabase().getExistingDocument("pit:" + teamKey);
            List<Document> matchDocuments = db.getMatchResultsForTeam(teamKey);
            ((TeamSummariesFragmentPagerAdapter) pager.getAdapter()).acceptNewTeamData(teamKey, teamDocument, pitDocument, matchDocuments);
        } catch (CouchbaseLiteException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error loading data for team. Check LogCat.", Toast.LENGTH_LONG).show();
        }
    }
}
