package org.wildstang.wildrank.androidv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nathan on 1/20/2015.
 */
public class MatchListAdapter extends ArrayAdapter<QueryRow> {

    public MatchListAdapter(Context context, List<QueryRow> matches) {
        super(context, R.layout.list_item_match, matches);
    }

    private static class ViewHolder {
        TextView matchNumber;
        TextView redTeams;
        TextView blueTeams;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater.from(getContext()));
            convertView = inflater.inflate(R.layout.list_item_match, null, false);
            holder = new ViewHolder();
            holder.matchNumber = (TextView) convertView.findViewById(R.id.match_number);
            holder.redTeams = (TextView) convertView.findViewById(R.id.red_alliance);
            holder.redTeams = (TextView) convertView.findViewById(R.id.blue_alliance);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QueryRow row = getItem(position);

        Map<String, Object> properties = row.getDocumentProperties();

        holder.matchNumber.setText(properties.get("match_number").toString());

        Map<String, Object> alliances = (Map<String, Object>) properties.get("alliances");

        Map<String, Object> redAlliance = (Map<String, Object>) alliances.get("red");
        ArrayList<Object> redTeams = (ArrayList<Object>) redAlliance.get("teams");

        String redTeamsConcat = "";
        for (Object team : redTeams) {
            redTeamsConcat.concat(team.toString().replace("frc", ""));
        }

        holder.redTeams.setText(redTeamsConcat);

        Map<String, Object> blueAlliance = (Map<String, Object>) alliances.get("blue");
        ArrayList<Object> blueTeams = (ArrayList<Object>) blueAlliance.get("teams");

        String blueTeamsConcat = "";
        for (Object team : blueTeams) {
            blueTeamsConcat.concat(team.toString().replace("frc", ""));
        }

        holder.blueTeams.setText(blueTeamsConcat);

        return convertView;
    }
}
