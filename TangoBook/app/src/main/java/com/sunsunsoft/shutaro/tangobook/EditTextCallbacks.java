package com.sunsunsoft.shutaro.tangobook;

/**
 * Created by shutaro on 2017/06/14.
 */

/**
 * エディットのコールバック
 */
public interface EditTextCallbacks {
    /**
     * 編集前イベント
     */
    void beforeTextChanged(String str, int start, int count, int after);
    /**
     * 編集後イベント
     */
    void onTextChanged(String str, int start, int before, int count);
    /**
     * 編集確定後イベント
     */
    void afterTextChanged(String str);
}
