package com.sunsunsoft.shutaro.tangobook.uview;

/**
 * Created by shutaro on 2017/06/14.
 * doActionメソッドをの戻り値
 */
public enum DoActionRet {
    None,               // 何も処理しない
    Redraw,             // 再描画あり(doActionループ処理を継続)
    Done                // 完了(doActionループ終了)
}
