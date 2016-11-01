package com.sunsunsoft.shutaro.testdb;

import android.graphics.Color;
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
            case 3:
                return TangoCardBookFragment.newInstance(Color.rgb(80,255,128));
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
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
            case 3:
                return "CardInBook";
        }
        return "blank";
    }
}