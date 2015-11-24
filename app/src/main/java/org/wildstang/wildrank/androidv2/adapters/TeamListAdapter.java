package org.wildstang.wildrank.androidv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.couchbase.lite.QueryRow;

import org.wildstang.wildrank.androidv2.R;
import org.wildstang.wildrank.androidv2.data.DatabaseManager;

import java.util.List;
import java.util.Map;

public class TeamListAdapter extends ArrayAdapter<QueryRow> {

    private boolean greyOutScoutedTeams;

    public TeamListAdapter(Context context, List<QueryRow> teams, boolean greyOutScoutedTeams) {
        super(context, R.layout.list_item_team, teams);
        this.greyOutScoutedTeams = greyOutScoutedTeams;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater.from(getContext()));
            convertView = inflater.inflate(R.layout.list_item_team, null, false);
            holder = new ViewHolder();
            holder.teamNumber = (TextView) convertView.findViewById(R.id.team_number);
            holder.teamName = (TextView) convertView.findViewById(R.id.team_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QueryRow row = getItem(position);

        Map<String, Object> properties = row.getDocument().getProperties();

        boolean isTeamScouted;
        try {
            isTeamScouted = DatabaseManager.getInstance(getContext()).isTeamPitScouted((String) properties.get("key"));
        } catch (Exception e) {
            e.printStackTrace();
            isTeamScouted = false;
        }

        holder.teamNumber.setText(properties.get("team_number").toString());

        holder.teamName.setText(properties.get("nickname").toString());

        // Gray everything out if the team has already been scouted
        float alpha;
        if (isTeamScouted && greyOutScoutedTeams) {
            alpha = 0.2f;
        } else {
            alpha = 1f;
        }
        holder.teamNumber.setAlpha(alpha);
        holder.teamName.setAlpha(alpha);


        return convertView;
    }

    private static class ViewHolder {
        TextView teamNumber;
        TextView teamName;
    }
}
