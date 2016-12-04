package com.sunsunsoft.shutaro.tangobook;

/**
 * Created by shutaro on 2016/11/28.
 */

public class PointL {
    public long x;
    public long y;

    public PointL() {}

    public PointL(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public PointL(PointL p) {
        this.x = p.x;
        this.y = p.y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public final void set(long x, long y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the point's x and y coordinates to the coordinates of p
     */
    public final void set(PointL p) {
        this.x = p.x;
        this.y = p.y;
    }

}
