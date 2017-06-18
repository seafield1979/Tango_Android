package com.sunsunsoft.shutaro.tangobook.uview.window;

/**
 * Created by shutaro on 2017/06/14.
 */

public enum LogWindowType {
    Movable,        // ドラッグで移動可能(クリックで表示切り替え)
    Fix,            // 固定位置に表示(ドラッグ移動不可、クリックで非表示にならない)
    AutoDisappear   // ログが追加されてから一定時間で消える
}
