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
                return TangoCardInBookFragment.newInstance(Color.rgb(80,255,128));
            case 4:
                return TangoItemInBoxFragment.newInstance(Color.rgb(100,100,200));
            case 5:
                return TangoItemPosFragment.newInstance(Color.rgb(0,100,200));
        }
        return TangoCardFragment.newInstance(android.R.color.holo_blue_bright);
    }

    @Override
    public int getCount() {
        return 6;
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
            case 4:
                return "In Box";
            case 5:
                return "pos";
        }
        return "blank";
    }
}