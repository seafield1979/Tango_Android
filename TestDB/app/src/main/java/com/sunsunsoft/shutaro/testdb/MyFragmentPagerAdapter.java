package com.sunsunsoft.shutaro.testdb;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by shutaro on 2016/10/19.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TangoCardFragment.newInstance(android.R.color.holo_blue_bright);
            case 1:
                return TangoBookFragment.newInstance(android.R.color.holo_green_light);
            case 2:
                return TangoBoxFragment.newInstance(android.R.color.holo_red_dark);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Card";
            case 1:
                return "Book";
            case 2:
                return "Box";
        }
        return "blank";
    }
}