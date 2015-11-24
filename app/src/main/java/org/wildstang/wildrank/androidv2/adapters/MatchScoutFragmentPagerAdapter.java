package org.wildstang.wildrank.androidv2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.wildstang.wildrank.androidv2.fragments.AutonomousScoutingFragment;
import org.wildstang.wildrank.androidv2.fragments.PostMatchScoutingFragment;
import org.wildstang.wildrank.androidv2.fragments.ScoutingFragment;
import org.wildstang.wildrank.androidv2.fragments.TeleopScoutingFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchScoutFragmentPagerAdapter extends FragmentPagerAdapter {
    private final String[] TITLES = {"Autonomous", "Teleop", "Post-Match"};

    private Map<Integer, WeakReference<ScoutingFragment>> fragments = new HashMap<>();

    public MatchScoutFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        ScoutingFragment fragment;
        switch (position) {
            case 0: // auto
                fragment = new AutonomousScoutingFragment();
                break;
            case 1: // teleop
                fragment = new TeleopScoutingFragment();
                break;
            case 2: // post match
                fragment = new PostMatchScoutingFragment();
                break;
            default: // uh oh.
                fragment = null;
                break;
        }
        fragments.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    public List<ScoutingFragment> getAllFragments() {
        List<ScoutingFragment> fragmentsList = new ArrayList<>();
        for (Map.Entry<Integer, WeakReference<ScoutingFragment>> entry : fragments.entrySet()) {
            fragmentsList.add(entry.getValue().get());
        }
        return fragmentsList;
    }
}