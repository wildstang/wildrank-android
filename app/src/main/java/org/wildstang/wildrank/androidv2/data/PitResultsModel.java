package org.wildstang.wildrank.androidv2.data;

import java.util.Map;

public class PitResultsModel {
    private String[] userIds;
    private String teamKey;
    private Map<String, Object> data;

    public PitResultsModel(String[] userIds, String teamKey, Map<String, Object> data) {
        this.userIds = userIds;
        this.teamKey = teamKey;
        this.data = data;
    }

    public String[] getUserIds() {
        return userIds;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
