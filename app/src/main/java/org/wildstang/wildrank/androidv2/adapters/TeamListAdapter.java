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

/**
 * Created by Nathan on 1/20/2015.
 */
public class TeamListAdapter extends ArrayAdapter<QueryRow> {

    public TeamListAdapter(Context context, List<QueryRow> matches) {
        super(context, R.layout.list_item_team, matches);
    }

    private static class ViewHolder {
        TextView teamNumber;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater.from(getContext()));
            convertView = inflater.inflate(R.layout.list_item_team, null, false);
            holder = new ViewHolder();
            holder.teamNumber = (TextView) convertView.findViewById(R.id.team_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QueryRow row = getItem(position);

        Map<String, Object> properties = row.getDocument().getProperties();

        boolean isTeamScouted;
        try {
            isTeamScouted = DatabaseManager.getInstance(getContext()).isTeamPitScouted((String) properties.get("team_key"));
        } catch (Exception e) {
            e.printStackTrace();
            isTeamScouted = false;
        }

        holder.teamNumber.setText(properties.get("team_number").toString());

        // Gray everything out if the team has already been scouted
        if (isTeamScouted) {
            float alpha = 0.2f;
            holder.teamNumber.setAlpha(alpha);
        } else {
            float alpha = 1f;
            holder.teamNumber.setAlpha(alpha);
        }

        return convertView;
    }
}
