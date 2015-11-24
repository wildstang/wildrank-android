package org.wildstang.wildrank.androidv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.Utilities;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.util.List;
import java.util.Map;

public class MatchListAdapter extends ArrayAdapter<QueryRow> {

    public MatchListAdapter(Context context, List<QueryRow> matches) {
        super(context, R.layout.list_item_match, matches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater.from(getContext()));
            convertView = inflater.inflate(R.layout.list_item_match, null, false);
            holder = new ViewHolder();
            holder.matchNumber = (TextView) convertView.findViewById(R.id.match_number);
            holder.red1 = (TextView) convertView.findViewById(R.id.red_team_1);
            holder.red2 = (TextView) convertView.findViewById(R.id.red_team_2);
            holder.red3 = (TextView) convertView.findViewById(R.id.red_team_3);
            holder.blue1 = (TextView) convertView.findViewById(R.id.blue_team_1);
            holder.blue2 = (TextView) convertView.findViewById(R.id.blue_team_2);
            holder.blue3 = (TextView) convertView.findViewById(R.id.blue_team_3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QueryRow row = getItem(position);

        Map<String, Object> properties = row.getDocument().getProperties();

        String selectedTeamKey = Utilities.getAssignedTeamKeyFromMatchDocument(getContext(), row.getDocument());
        String matchKey = properties.get("key").toString();

        boolean isMatchScouted;
        try {
            isMatchScouted = DatabaseManager.getInstance(getContext()).isMatchScouted(matchKey, selectedTeamKey);
        } catch (Exception e) {
            e.printStackTrace();
            isMatchScouted = false;
        }

        holder.matchNumber.setText(properties.get("match_number").toString());

        Object[] redTeams = Utilities.getRedTeamsFromMatchDocument(row.getDocument());
        holder.red1.setText(redTeams[0].toString().replace("frc", ""));
        holder.red2.setText(redTeams[1].toString().replace("frc", ""));
        holder.red3.setText(redTeams[2].toString().replace("frc", ""));

        Object[] blueTeams = Utilities.getBlueTeamsFromMatchDocument(row.getDocument());

        holder.blue1.setText(blueTeams[0].toString().replace("frc", ""));
        holder.blue2.setText(blueTeams[1].toString().replace("frc", ""));
        holder.blue3.setText(blueTeams[2].toString().replace("frc", ""));

        // Gray everything out if the mathc has already been scouted
        if (isMatchScouted) {
            float alpha = 0.2f;
            holder.matchNumber.setAlpha(alpha);
            holder.red1.setAlpha(alpha);
            holder.red2.setAlpha(alpha);
            holder.red3.setAlpha(alpha);
            holder.blue1.setAlpha(alpha);
            holder.blue2.setAlpha(alpha);
            holder.blue3.setAlpha(alpha);
        } else {
            holder.matchNumber.setAlpha(1f);
            holder.red1.setAlpha(1f);
            holder.red2.setAlpha(1f);
            holder.red3.setAlpha(1f);
            holder.blue1.setAlpha(1f);
            holder.blue2.setAlpha(1f);
            holder.blue3.setAlpha(1f);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView matchNumber;
        TextView red1;
        TextView red2;
        TextView red3;
        TextView blue1;
        TextView blue2;
        TextView blue3;
    }
}
