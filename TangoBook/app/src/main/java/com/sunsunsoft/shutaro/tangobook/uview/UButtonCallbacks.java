package com.sunsunsoft.shutaro.tangobook.uview;

/**
 * Created by shutaro on 2017/06/14.
 */

public interface UButtonCallbacks {
    /**
     * ボタンがクリックされた時の処理
     * @param id  button id
     * @param pressedOn  押された状態かどうか(On/Off)
     * @return
     */
    boolean UButtonClicked(int id, boolean pressedOn);
}