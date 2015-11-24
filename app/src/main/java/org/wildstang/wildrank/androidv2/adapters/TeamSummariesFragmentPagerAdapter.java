package org.wildstang.wildrank.androidv2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.couchbase.lite.Document;

import org.wildstang.wildrank.androidv2.fragments.TeamSummariesDataFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesInfoFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesRawDataFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesStackFragment;

import java.util.List;

public class TeamSummariesFragmentPagerAdapter extends FragmentStatePagerAdapter {

    static final int NUM_FRAGMENTS = 4;

    private TeamSummariesInfoFragment infoFragment;
    private TeamSummariesDataFragment dataFragment;
    private TeamSummariesStackFragment stackFragment;
    private TeamSummariesRawDataFragment rawDataFragment;

    public TeamSummariesFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        dataFragment = new TeamSummariesDataFragment();
        rawDataFragment = new TeamSummariesRawDataFragment();
        stackFragment = new TeamSummariesStackFragment();
        infoFragment = new TeamSummariesInfoFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return infoFragment;
            case 1:
                return dataFragment;
            case 2:
                return stackFragment;
            case 3:
                return rawDataFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return NUM_FRAGMENTS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "General";
            case 1:
                return "Data";
            case 2:
                return "Stacks";
            case 3:
                return "Raw Data";
            default:
                return "ERROR INVALID POSITION";
        }
    }

    public void acceptNewTeamData(String teamKey, Document teamDoc, Document pitDoc, List<Document> matchDocs) {
        infoFragment.acceptNewTeamData(teamKey, teamDoc, pitDoc, matchDocs);
        dataFragment.acceptNewTeamData(teamKey, teamDoc, pitDoc, matchDocs);
        stackFragment.acceptNewTeamData(teamKey, teamDoc, pitDoc, matchDocs);
        rawDataFragment.acceptNewTeamData(teamKey, teamDoc, pitDoc, matchDocs);
    }

}