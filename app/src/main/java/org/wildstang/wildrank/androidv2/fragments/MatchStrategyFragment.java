package org.wildstang.wildrank.androidv2.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.adapters.TeamSummariesFragmentPagerAdapter;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.models.TeamDocumentsModel;
import org.wildstang.wildrank.androidv2.views.SlidingTabs;
import org.wildstang.wildrank.androidv2.views.TeamStrategyView;
import org.wildstang.wildrank.androidv2.views.data.StackDataView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchStrategyFragment extends Fragment {

    private static final String MATCH_KEY = "match_key";

    public List<TeamStrategyView> mStrategyViews = new ArrayList<>();
    private String mMatchKey;
    //String[] teams;
    StackDataView stackView;
    Button exitFocusButton;
    LinearLayout focusLayout;
    List<Document> currentTeamDocs;
    public static MatchStrategyFragment newInstance(String matchKey) {
        MatchStrategyFragment f = new MatchStrategyFragment();
        Bundle b = new Bundle();
        b.putString(MATCH_KEY, matchKey);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            mMatchKey = getArguments().getString(MATCH_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_strategy, container, false);
        focusLayout = (LinearLayout) view.findViewById(R.id.focus);
        stackView = (StackDataView) view.findViewById(R.id.stackviewstrategy);
        exitFocusButton = (Button) view.findViewById(R.id.exit_team_focus);
        exitFocusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTeamFocused();
            }
        });
//        stackView.setVisibility(View.GONE);
//        exitFocusButton.setVisibility(View.GONE);
        focusLayout.setVisibility(View.GONE);
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.one));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.two));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.three));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.four));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.five));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.six));
//        mStrategyViews.get(0).setBackgroundColor(Color.WHITE);
//        mStrategyViews.get(1).setBackgroundColor(Color.WHITE);
//        mStrategyViews.get(2).setBackgroundColor(Color.WHITE);
//        mStrategyViews.get(3).setBackgroundColor(Color.WHITE);
//        mStrategyViews.get(4).setBackgroundColor(Color.WHITE);
//        mStrategyViews.get(5).setBackgroundColor(Color.WHITE);
        try {
            Document matchDoc = DatabaseManager.getInstance(getContext()).getMatchFromKey(mMatchKey);
            Object[] teamObjects = Utilities.getTeamsFromMatchDocument(matchDoc);
            String[] teamKeys = Arrays.copyOf(teamObjects, teamObjects.length, String[].class);
            mStrategyViews.get(0).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadInfoForTeam(teamKeys[0]);
                    stackView.acceptNewTeamData(currentTeamDocs);
                    toggleTeamFocused();

                }
            });
            mStrategyViews.get(1).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadInfoForTeam(teamKeys[1]);
                    stackView.acceptNewTeamData(currentTeamDocs);
                    toggleTeamFocused();

                }
            });
            mStrategyViews.get(2).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadInfoForTeam(teamKeys[2]);
                    stackView.acceptNewTeamData(currentTeamDocs);
                    toggleTeamFocused();

                }
            });
            mStrategyViews.get(3).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadInfoForTeam(teamKeys[3]);
                    stackView.acceptNewTeamData(currentTeamDocs);
                    toggleTeamFocused();

                }
            });
            mStrategyViews.get(4).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadInfoForTeam(teamKeys[4]);
                    stackView.acceptNewTeamData(currentTeamDocs);
                    toggleTeamFocused();

                }
            });
            mStrategyViews.get(5).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadInfoForTeam(teamKeys[5]);
                    stackView.acceptNewTeamData(currentTeamDocs);
                    toggleTeamFocused();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error loading match document", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void onTeamSelected(Document doc) {
        loadInfoForTeam((String) doc.getProperty("key"));

    }

    private void loadInfoForTeam(String teamKey) {
        try {
//            View view = (StackDataView)
//            tabs = (SlidingTabs) view.findViewById(R.id.tabs);
//            pager = (ViewPager) view.findViewById(R.id.view_pager);
//            pager.setAdapter(new TeamSummariesFragmentPagerAdapter(getFragmentManager()));
//            tabs.setViewPager(pager);
//            Log.d("Test", teamKey);
            currentTeamDocs = new ArrayList<Document>();
            DatabaseManager db = DatabaseManager.getInstance(getActivity());
            currentTeamDocs = db.getMatchResultsForTeam(teamKey);

        } catch (CouchbaseLiteException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error loading data for team. Check LogCat.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Load the data for this match
        try {
            Document matchDoc = DatabaseManager.getInstance(getContext()).getMatchFromKey(mMatchKey);
            Object[] teamObjects = Utilities.getTeamsFromMatchDocument(matchDoc);
            String[] teamKeys = Arrays.copyOf(teamObjects, teamObjects.length, String[].class);
            for (int i = 0; i < mStrategyViews.size(); i++) {
                mStrategyViews.get(i).populateFromTeamDocuments(TeamDocumentsModel.from(getContext(), teamKeys[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error loading match document", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isTeamFocus() {
        if (focusLayout.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    public void toggleTeamFocused() {
        if (isTeamFocus()) {
//            stackView.setVisibility(View.GONE);
//            exitFocusButton.setVisibility(View.GONE);
            focusLayout.setVisibility(View.GONE);
        } else {
//            stackView.setVisibility(View.VISIBLE);
//            exitFocusButton.setVisibility(View.VISIBLE);
            focusLayout.setVisibility(View.VISIBLE);
        }
    }
}