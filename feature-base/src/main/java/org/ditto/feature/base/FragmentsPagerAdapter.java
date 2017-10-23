package org.ditto.feature.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.ditto.lib.Constants;

import java.util.Map;

import dagger.internal.Preconditions;

public class FragmentsPagerAdapter extends FragmentStatePagerAdapter {

    private Map<Integer, Fragment> fragments;

    public FragmentsPagerAdapter(FragmentManager fm, Map<Integer, Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Preconditions.checkNotNull(getItem(position).getArguments().getString(Constants.TITLE));
    }

}