package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;
import org.wildstang.wildrank.androidv2.models.TeamDocumentsModel;
import org.wildstang.wildrank.androidv2.views.TeamStrategyView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchStrategyFragment extends Fragment {

    private static final String MATCH_KEY = "match_key";

    public List<TeamStrategyView> mStrategyViews = new ArrayList<>();
    private String mMatchKey;
    //String[] teams;

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

        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.one));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.two));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.three));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.four));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.five));
        mStrategyViews.add((TeamStrategyView) view.findViewById(R.id.six));

        return view;
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
}