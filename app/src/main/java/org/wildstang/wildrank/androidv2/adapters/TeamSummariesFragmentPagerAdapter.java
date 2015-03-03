package org.wildstang.wildrank.androidv2.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.w3c.dom.Document;
import org.wildstang.wildrank.androidv2.fragments.AutonomousScoutingFragment;
import org.wildstang.wildrank.androidv2.fragments.PostMatchScoutingFragment;
import org.wildstang.wildrank.androidv2.fragments.ScoutingFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesDataFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesGraphFragment;
import org.wildstang.wildrank.androidv2.fragments.TeamSummariesNotesFragment;
import org.wildstang.wildrank.androidv2.fragments.TeleopScoutingFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liam on 2/28/2015.
 */
public class TeamSummariesFragmentPagerAdapter extends FragmentStatePagerAdapter
{

    static final int NUM_FRAGMENTS = 3;
    private Bundle fragmentArgs;

    private TeamSummariesDataFragment dataFragment;
    private TeamSummariesGraphFragment graphFragment;
    private TeamSummariesNotesFragment notesFragment;

    public TeamSummariesFragmentPagerAdapter(FragmentManager fm, String teamID)
    {
        super(fm);
        System.out.println("team id: " + teamID);
        initFragments(teamID);
    }

    private void initFragments(String teamID)
    {
        fragmentArgs = new Bundle();
        fragmentArgs.putString("TeamID", teamID);
        dataFragment = new TeamSummariesDataFragment();
        dataFragment.setArguments(fragmentArgs);
        graphFragment = new TeamSummariesGraphFragment();
        graphFragment.setArguments(fragmentArgs);
        notesFragment = new TeamSummariesNotesFragment();
        notesFragment.setArguments(fragmentArgs);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
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
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        return NUM_FRAGMENTS;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
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

    public void changeTeamID(String newTeamID)
    {
        initFragments(newTeamID);
        notifyDataSetChanged();
    }

}