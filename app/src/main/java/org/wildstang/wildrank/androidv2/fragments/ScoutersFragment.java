package org.wildstang.wildrank.androidv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mail929 on 4/3/15.
 */
public class ScoutersFragment  extends Fragment
{

    private ListView list;
    private ListView scouters;
    private TextView red1;
    private TextView red2;
    private TextView red3;
    private TextView blue1;
    private TextView blue2;
    private TextView blue3;
    private TextView red1Scouter;
    private TextView red2Scouter;
    private TextView red3Scouter;
    private TextView blue1Scouter;
    private TextView blue2Scouter;
    private TextView blue3Scouter;
    private TextView matchNum;

    List<Scouter> scoutCounter;
    HashMap<Integer, MatchModel> matches;

    public ScoutersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scouters, container, false);
/*
        matches = new HashMap<>();
        try {
            List<TeamMatchModel> data = runQuery();
            for(int i = 0; i < data.size(); i++)
            {
                TeamMatchModel dataPoint = data.get(i);
                boolean foundMatch = false;
                if(matches.get(dataPoint.matchNumber) != null) {
                    MatchModel match = matches.get(dataPoint.matchNumber);
                    match.add(dataPoint.teamNumber, dataPoint.scouters);
                } else {
                    MatchModel match = new MatchModel(dataPoint.matchNumber);
                    match.add(dataPoint.teamNumber, dataPoint.scouters);
                    matches.put(dataPoint.matchNumber, match);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sortMatches(matches);

        list = (ListView) view.findViewById(R.id.matches);
        list.setAdapter(new ArrayAdapter<MatchModel>(getActivity(), R.layout.list_item_scouters, R.id.red1, matches) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null) {
                    LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    convertView = infl.inflate(R.layout.list_item_scouters, parent, false);
                }
                view = super.getView(position, convertView, parent);

                MatchModel match = matches.get(position);
                matchNum = (TextView) view.findViewById(R.id.match_number);
                matchNum.setText(match.matchNumber);
                if(match.teamScoutersMap.size() >= 1)
                {
                    red1 = (TextView) view.findViewById(R.id.red1);
                    red1.setText(match.teamScoutersMap.get(0));
                    red1Scouter = (TextView) view.findViewById(R.id.red1Scouter);
                    red1Scouter.setText(match.scouters.get(0));
                }

                if(match.scouters.size() >= 2)
                {
                    red2 = (TextView) view.findViewById(R.id.red2);
                    red2.setText(match.teams.get(1) );
                    red2Scouter = (TextView) view.findViewById(R.id.red2Scouter);
                    red2Scouter.setText(match.scouters.get(1));
                }

                if(match.scouters.size() >= 3)
                {
                    red3 = (TextView) view.findViewById(R.id.red3);
                    red3.setText(match.teams.get(2));
                    red3Scouter = (TextView) view.findViewById(R.id.red3Scouter);
                    red3Scouter.setText(match.scouters.get(2));
                }

                if(match.scouters.size() >= 4)
                {
                    blue1 = (TextView) view.findViewById(R.id.blue1);
                    blue1.setText(match.teams.get(3));
                    blue1Scouter = (TextView) view.findViewById(R.id.blue1Scouter);
                    blue1Scouter.setText(match.scouters.get(3));
                }

                if(match.scouters.size() >= 5)
                {
                    blue2 = (TextView) view.findViewById(R.id.blue2);
                    blue2.setText(match.teams.get(4));
                    blue2Scouter = (TextView) view.findViewById(R.id.blue2Scouter);
                    blue2Scouter.setText(match.scouters.get(4));
                }

                if(match.scouters.size() >= 6)
                {
                    blue3 = (TextView) view.findViewById(R.id.blue3);
                    blue3.setText(match.teams.get(5));
                    blue3Scouter = (TextView) view.findViewById(R.id.blue3Scouter);
                    blue3Scouter.setText(match.scouters.get(5));
                }
                return view;
            }
        });

        scoutCounter = sortScouters(scoutCounter);
        scouters = (ListView) view.findViewById(R.id.scouters);
        scouters.setAdapter(new ArrayAdapter<Scouter>(getActivity(), R.layout.list_item_scouter, R.id.scouter, scoutCounter) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null) {
                    LayoutInflater infl = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                    convertView = infl.inflate(R.layout.list_item_scouter, parent, false);
                }
                view = super.getView(position, convertView, parent);

                Scouter scouter = scoutCounter.get(position);

                TextView scouterName = (TextView) view.findViewById(R.id.scouter);
                scouterName.setText(scouter.scouterName);
                TextView count = (TextView) view.findViewById(R.id.count);
                count.setText(Integer.toString(scouter.matchCount));

                return view;
            }
        });*/
        return view;
    }

    private List<TeamMatchModel> runQuery() throws Exception {
        Query query = DatabaseManager.getInstance(getActivity()).getAllCompleteMatches();

        List<TeamMatchModel> data = new ArrayList<>();
        QueryEnumerator enumerator = query.run();

        scoutCounter = new ArrayList<>();

        for (Iterator<QueryRow> it = enumerator; it.hasNext(); ) {
            QueryRow row = it.next();
            int teamNumber = Utilities.teamNumberFromTeamKey(row.getDocument().getProperty("team_key").toString());
            int matchNumber = Utilities.matchNumberFromMatchKey(row.getDocument().getProperty("match_key").toString());
            List<String> scouterIds = (ArrayList<String>) row.getDocument().getProperty("users");
            List<String> scouterNames = new ArrayList<>();
            for(String scouterId : scouterIds)
            {
                String scouterName = DatabaseManager.getInstance(getActivity()).getUserById(scouterId).getProperty("name").toString();
                scouterNames.add(scouterName);
                boolean exists = false;
                for(Scouter scouter : scoutCounter)
                {
                    if(scouterName.equals(scouter.scouterName))
                    {
                        exists = true;
                        scouter.incrementMatchCount();
                    }
                }
                if(!exists)
                {
                    scoutCounter.add(new Scouter(scouterName));
                }
            }
            data.add(new TeamMatchModel(teamNumber, matchNumber, scouterNames));
            //System.out.println("Row " + counter + " is match " + match + " for team " + team);
        }
        return data;
    }

    public void sortScouters(List<Scouter> scouters)
    {
        //Collections.sort(scouters, (lhs, rhs) -> Integer.compare(lhs.matchCount, rhs.matchCount));
    }

    public void sortMatches(List<MatchModel> matches)
    {
        //Collections.sort(matches, (lhs, rhs) -> Integer.compare(lhs.matchNumber, rhs.matchNumber));
    }

    public class TeamMatchModel
    {
        int teamNumber;
        int matchNumber;
        List<String> scouters;

        public TeamMatchModel(int teamNumber, int matchNumber, List<String> scouters)
        {
            this.teamNumber = teamNumber;
            this.matchNumber = matchNumber;
            this.scouters = scouters;
        }
    }

    public class MatchModel
    {
        int matchNumber;
        Map<Integer, List<String>> teamScoutersMap = new HashMap<>();

        public MatchModel(int matchNumber)
        {
            this.matchNumber = matchNumber;
        }

        public void add(int teamNumber, List<String> scouters) {
            teamScoutersMap.put(teamNumber, scouters);
        }

        public String getScoutersForIndex(int index) {
            String scouters = "";
            //List<String> scoutersList = teamScoutersMap;
            for(int i = 0;;)
            {

            }
        }
    }

    public class Scouter
    {
        String scouterName;
        int matchCount;

        public Scouter(String scouterName)
        {
            this.scouterName = scouterName;
            matchCount = 1;
        }

        public void incrementMatchCount()
        {
            matchCount++;
        }
    }
}