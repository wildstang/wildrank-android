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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.adapters.MatchScoutFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.adapters.TeamListAdapter;
import org.wildstang.wildrank.androidv2.adapters.TeamSummariesFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.views.SlidingTabs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesMainFragment extends Fragment
{
    private ListView teamList;
    private ListView pickList;
    private ViewPager pager;
    private SlidingTabs tabs;

    private TeamListAdapter listAdapter;
    private TeamSummariesFragmentPagerAdapter pagerAdapter;

    private String selectedTeamKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_summaries_main, container, false);
        teamList = (ListView) view.findViewById(R.id.teams_list);
        pickList = (ListView) view.findViewById(R.id.pick_list);
        pager = (ViewPager) view.findViewById(R.id.view_pager);
        tabs = (SlidingTabs) view.findViewById(R.id.tabs);

        pager.setOffscreenPageLimit(10);
        teamList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                QueryRow row = (QueryRow) parent.getItemAtPosition(position);
                onTeamSelected(row.getDocument());
            }
        });

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
            Log.d("wildstang", "Document key: " + row.getKey());
        }

        Parcelable state = teamList.onSaveInstanceState();
        listAdapter = new TeamListAdapter(getActivity(), queryRows);
        teamList.setAdapter(listAdapter);
        teamList.onRestoreInstanceState(state);

        state = pickList.onSaveInstanceState();
        listAdapter = new TeamListAdapter(getActivity(), queryRows);
        pickList.setAdapter(listAdapter);
        pickList.onRestoreInstanceState(state);
    }

    private void onTeamSelected(Document doc)
    {
        loadInfoForTeam((String) doc.getProperty("team_key"));
    }

    private void loadInfoForTeam(String teamID)
    {
        int position = pager.getCurrentItem();
        if(pager.getAdapter() == null)
        {
            pager.setAdapter(new TeamSummariesFragmentPagerAdapter(getFragmentManager(), teamID));
        }
        else
        {
            ((TeamSummariesFragmentPagerAdapter) pager.getAdapter()).changeTeamID(teamID);
        }
        pager.setCurrentItem(position);
        tabs.setViewPager(pager);
    }
}
