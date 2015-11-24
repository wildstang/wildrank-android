package org.wildstang.wildrank.androidv2.data;

import java.util.Map;

public class MatchResultsModel {
    private String[] userIds;
    private String matchKey;
    private String teamKey;
    private Map<String, Object> data;

    public MatchResultsModel(String[] userIds, String matchKey, String teamKey, Map<String, Object> data) {
        this.userIds = userIds;
        this.matchKey = matchKey;
        this.teamKey = teamKey;
        this.data = data;
    }

    public String[] getUserIds() {
        return userIds;
    }

    public String getMatchKey() {
        return matchKey;
    }

    public String getTeamKey() {
        return teamKey;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
