package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;

/**
 * Created by shutaro on 2017/01/22.
 *
 * ヘルプページのListViewのアイテムの情報
 */

public class HelpListItemData {
    private Bitmap mImageData;
    private String mText;
    private boolean isTitle;
    private int mTextColor;
    private int mBgColor;

    private static int count = 0;

    public HelpListItemData(String text, boolean isTitle, int textColor, int bgColor) {
        mImageData = null;
        mText = text;
        this.isTitle = isTitle;
        mTextColor = textColor;
        mBgColor = bgColor;

        HelpListItemData.count++;
    }

    public Bitmap getImageData() {
        return mImageData;
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
