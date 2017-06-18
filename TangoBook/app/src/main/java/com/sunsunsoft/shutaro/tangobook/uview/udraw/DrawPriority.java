package com.sunsunsoft.shutaro.tangobook.uview.udraw;

/**
 * Created by shutaro on 2017/06/14.
 */


/**
 * 描画優先度
 */
public enum DrawPriority {
    Dialog(5),
    DragIcon(11),
    IconWindow(100),
    ;

    private final int priority;

    DrawPriority(final int priority) {
        this.priority = priority;
    }

    public int p() {
        return this.priority;
    }
}
