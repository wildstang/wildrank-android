package org.wildstang.wildrank.androidv2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.wildstang.wildrank.androidv2.fragments.TeamSummariesDataFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesGraphFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesInfoFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesStackFragment;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesFragmentPagerAdapter extends FragmentStatePagerAdapter {

    static final int NUM_FRAGMENTS = 4;

    private TeamSummariesInfoFragment infoFragment;
    private TeamSummariesDataFragment dataFragment;
    private TeamSummariesGraphFragment graphFragment;
    private TeamSummariesStackFragment stackFragment;

    public TeamSummariesFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        dataFragment = new TeamSummariesDataFragment();
        graphFragment = new TeamSummariesGraphFragment();
        infoFragment = new TeamSummariesInfoFragment();
        stackFragment = new TeamSummariesStackFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return infoFragment;
            case 1:
                return dataFragment;
            case 2:
                return graphFragment;
            case 3:
                return stackFragment;
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
                return "Graphs";
            case 3:
                return "Stacks";
            default:
                return "ERROR INVALID POSITION";
        }
    }

    public void updateTeamKey(String newTeamKey) {
        dataFragment.updateTeamKey(newTeamKey);
        graphFragment.updateTeamKey(newTeamKey);
        infoFragment.updateTeamKey(newTeamKey);
        stackFragment.updateTeamKey(newTeamKey);
    }

}