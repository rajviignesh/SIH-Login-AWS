/*
 * Developed by Keivan Kiyanfar on 19/7/20 6:44 AM
 * Last modified 19/7/20 6:44 AM
 * Copyright (c) 2020. All rights reserved.
 */

package de.cryptiot.indoorfarming2;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Monitoring moniFragment = new Monitoring();
                return moniFragment;
            case 1:
                locationmap locoFragment = new locationmap();
                return locoFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Monitoring";
            case 1:
                return "Geotracking";
        }
        return null;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
