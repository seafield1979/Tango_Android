package com.sunsunsoft.shutaro.tangobook.util;

/**
 * Created by shutaro on 2017/06/14.
 * 自前のLong型のサイズクラス
 */


public class SizeL {
    public long width, height;

    public SizeL() {}
    public SizeL(long width, long height) {
        this.width = width;
        this.height = height;
    }
    public SizeL(Size _size) {
        this.width = _size.width;
        this.height = _size.height;
    }
    public void setWidth(long width) {
        this.width = width;
    }
    public void setHeight(long height) {
        this.height = height;
    }
}