package com.sunsunsoft.shutaro.tangobook;

/**
 * Created by shutaro on 2016/10/24.
 */

public class Size {
    public int width, height;

    public Size() {}
    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public Size(Size _size) {
        this.width = _size.width;
        this.height = _size.height;
    }
}

class SizeL {
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
}