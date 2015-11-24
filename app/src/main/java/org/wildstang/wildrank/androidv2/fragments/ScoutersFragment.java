package org.wildstang.wildrank.androidv2.fragments;

import android.content.Context;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScoutersFragment extends Fragment {

    List<Scouter> scoutCounter;
    private ListView list;
    private ListView scouters;

    public ScoutersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scouters, container, false);

        Map<Integer, MatchModel> matches = new HashMap<>();
        try {
            List<TeamMatchModel> data = runQuery();
            for (int i = 0; i < data.size(); i++) {
                TeamMatchModel matchTeam = data.get(i);
                if (matches.get(matchTeam.matchNumber) != null) {
                    MatchModel match = matches.get(matchTeam.matchNumber);
                    match.add(matchTeam);
                } else {
                    MatchModel match = new MatchModel(matchTeam.matchNumber);
                    match.add(matchTeam);
                    matches.put(matchTeam.matchNumber, match);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<MatchModel> matchList = sortMatchesAndGetAsList(matches);

        list = (ListView) view.findViewById(R.id.matches);
        list.setAdapter(new MatchScoutersListAdapter(getActivity(), R.layout.list_item_scouters, matchList));

        sortScouters(scoutCounter);

        scouters = (ListView) view.findViewById(R.id.scouters);
        scouters.setAdapter(new ScoutersListAdapter(getActivity(), R.layout.list_item_scouter, scoutCounter));
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
            for (String scouterId : scouterIds) {
                String scouterName = DatabaseManager.getInstance(getActivity()).getUserById(scouterId).getProperty("name").toString();
                scouterNames.add(scouterName);
                boolean exists = false;
                for (Scouter scouter : scoutCounter) {
                    if (scouterName.equals(scouter.scouterName)) {
                        exists = true;
                        scouter.incrementMatchCount();
                    }
                }
                if (!exists) {
                    scoutCounter.add(new Scouter(scouterName));
                }
            }
            data.add(new TeamMatchModel(teamNumber, matchNumber, scouterNames));
        }
        return data;
    }

    public void sortScouters(List<Scouter> scouters) {
        Collections.sort(scouters, (lhs, rhs) -> rhs.matchCount - lhs.matchCount);
    }

    public List<MatchModel> sortMatchesAndGetAsList(Map<Integer, MatchModel> matches) {
        List<MatchModel> matchList = new ArrayList<>(matches.values());
        Collections.sort(matchList, (lhs, rhs) -> lhs.matchNumber - rhs.matchNumber);
        return matchList;
    }

    public class TeamMatchModel {
        int teamNumber;
        int matchNumber;
        List<String> scouters;

        public TeamMatchModel(int teamNumber, int matchNumber, List<String> scouters) {
            this.teamNumber = teamNumber;
            this.matchNumber = matchNumber;
            this.scouters = scouters;
        }

        public String getTeamAsString() {
            return String.valueOf(teamNumber);
        }

        public String getScoutersAsString() {
            String scoutersString = "";
            if (scouters.size() > 0) {
                scoutersString += scouters.get(0);
                for (int i = 1; i < scouters.size(); i++) {
                    scoutersString += (", " + scouters.get(i));
                }
            }
            return scoutersString;
        }
    }

    public class MatchModel {
        int matchNumber;
        List<TeamMatchModel> matchTeams = new ArrayList<>();

        public MatchModel(int matchNumber) {
            this.matchNumber = matchNumber;
        }

        public void add(TeamMatchModel matchTeam) {
            matchTeams.add(matchTeam);
        }
    }

    public class Scouter {
        String scouterName;
        int matchCount;

        public Scouter(String scouterName) {
            this.scouterName = scouterName;
            matchCount = 1;
        }

        public void incrementMatchCount() {
            matchCount++;
        }
    }

    private class ScoutersListAdapter extends ArrayAdapter<Scouter> {

        public ScoutersListAdapter(Context context, int resource, List<Scouter> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_scouter, parent, false);
            }
            //view = super.getView(position, convertView, parent);
            view = convertView;

            Scouter scouter = getItem(position);

            TextView scouterName = (TextView) view.findViewById(R.id.scouterName);
            scouterName.setText(scouter.scouterName);
            TextView count = (TextView) view.findViewById(R.id.count);
            count.setText(Integer.toString(scouter.matchCount));

            return view;
        }
    }

    private class MatchScoutersListAdapter extends ArrayAdapter<MatchModel> {

        public MatchScoutersListAdapter(Context context, int resource, List<MatchModel> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_scouters, parent, false);
            }
            //view = super.getView(position, convertView, parent);
            view = convertView;

            MatchModel match = getItem(position);
            TextView matchNum = (TextView) view.findViewById(R.id.match_number);
            matchNum.setText(Integer.toString(match.matchNumber));

            if (match.matchTeams.size() >= 1) {
                TeamMatchModel tm = match.matchTeams.get(0);
                TextView red1 = (TextView) view.findViewById(R.id.red1);
                red1.setText(tm.getTeamAsString());
                TextView red1Scouter = (TextView) view.findViewById(R.id.red1Scouter);
                red1Scouter.setText(tm.getScoutersAsString());
            }

            if (match.matchTeams.size() >= 2) {
                TeamMatchModel tm = match.matchTeams.get(1);
                TextView red2 = (TextView) view.findViewById(R.id.red2);
                red2.setText(tm.getTeamAsString());
                TextView red2Scouter = (TextView) view.findViewById(R.id.red2Scouter);
                red2Scouter.setText(tm.getScoutersAsString());
            }

            if (match.matchTeams.size() >= 3) {
                TeamMatchModel tm = match.matchTeams.get(2);
                TextView red3 = (TextView) view.findViewById(R.id.red3);
                red3.setText(tm.getTeamAsString());
                TextView red3Scouter = (TextView) view.findViewById(R.id.red3Scouter);
                red3Scouter.setText(tm.getScoutersAsString());
            }

            if (match.matchTeams.size() >= 4) {
                TeamMatchModel tm = match.matchTeams.get(3);
                TextView blue1 = (TextView) view.findViewById(R.id.blue1);
                blue1.setText(tm.getTeamAsString());
                TextView blue1Scouter = (TextView) view.findViewById(R.id.blue1Scouter);
                blue1Scouter.setText(tm.getScoutersAsString());
            }

            if (match.matchTeams.size() >= 5) {
                TeamMatchModel tm = match.matchTeams.get(4);
                TextView blue2 = (TextView) view.findViewById(R.id.blue2);
                blue2.setText(tm.getTeamAsString());
                TextView blue2Scouter = (TextView) view.findViewById(R.id.blue2Scouter);
                blue2Scouter.setText(tm.getScoutersAsString());
            }

            if (match.matchTeams.size() >= 6) {
                TeamMatchModel tm = match.matchTeams.get(5);
                TextView blue3 = (TextView) view.findViewById(R.id.blue3);
                blue3.setText(tm.getTeamAsString());
                TextView blue3Scouter = (TextView) view.findViewById(R.id.blue3Scouter);
                blue3Scouter.setText(tm.getScoutersAsString());
            }

            return view;
        }
    }
}