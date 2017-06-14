package com.sunsunsoft.shutaro.tangobook.icon;

/**
 * Created by shutaro on 2017/06/14.
 */
/**
 * アイコンをクリックしたりドロップした時のコールバック
 */
public interface UIconCallbacks {
    void iconClicked(UIcon icon);
    void longClickIcon(UIcon icon);
    void iconDroped(UIcon icon);
}
