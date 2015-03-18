package org.wildstang.wildrank.androidv2.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.wildstang.wildrank.androidv2.fragments.TeamSummariesDataFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesGraphFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesNotesFragment;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesFragmentPagerAdapter extends FragmentStatePagerAdapter {

    static final int NUM_FRAGMENTS = 3;
    private Bundle fragmentArgs;

    private TeamSummariesDataFragment dataFragment;
    private TeamSummariesGraphFragment graphFragment;
    private TeamSummariesNotesFragment notesFragment;

    public TeamSummariesFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        dataFragment = new TeamSummariesDataFragment();
        graphFragment = new TeamSummariesGraphFragment();
        notesFragment = new TeamSummariesNotesFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return notesFragment;
            case 1:
                return dataFragment;
            case 2:
                return graphFragment;
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
            default:
                return "ERROR INVALID POSITION";
        }
    }

    public void updateTeamKey(String newTeamKey) {
        dataFragment.updateTeamKey(newTeamKey);
        graphFragment.updateTeamKey(newTeamKey);
        notesFragment.updateTeamKey(newTeamKey);
    }

}