package com.sunsunsoft.shutaro.tangobook.help;

import android.graphics.Bitmap;

/**
 * Created by shutaro on 2017/01/22.
 *
 * ヘルプページのListViewのアイテムの情報
 */

public class HelpListItemData {
    private String mText;
    private boolean isTitle;
    private int mTextColor;
    private int mBgColor;

    public HelpListItemData(String text, boolean isTitle, int textColor, int bgColor) {
        mText = text;
        this.isTitle = isTitle;
        mTextColor = textColor;
        mBgColor = bgColor;
    }

    public String getmText() {
        return mText;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getBgColor() {
        return mBgColor;
    }
}
