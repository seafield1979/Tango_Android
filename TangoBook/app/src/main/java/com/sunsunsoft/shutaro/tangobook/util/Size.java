package com.sunsunsoft.shutaro.tangobook.util;

/**
 * Created by shutaro on 2016/10/24.
 * 自前のサイズクラス
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
