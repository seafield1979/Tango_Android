package com.sunsunsoft.shutaro.tangobook;

/**
 * Created by shutaro on 2016/10/24.
 */

public class Size {
    private int mWidth, mHeight;

    public Size(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth(){ return mWidth; }
    public int getHeight() { return mHeight; }

    public void setWidth(int width) {
        mWidth = width;
    }
    public void setHeight(int height) {
        mHeight = height;
    }
}
