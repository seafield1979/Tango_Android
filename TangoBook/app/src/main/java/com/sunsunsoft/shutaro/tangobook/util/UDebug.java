package com.sunsunsoft.shutaro.tangobook.util;

import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.preset.PresetBookManager;

/**
 * Created by shutaro on 2016/11/07.
 */

public class UDebug {
    // Debug mode
    public static final boolean isDebug = true;

    // IconをまとめたブロックのRECTを描画するかどうか
    public static final boolean DRAW_ICON_BLOCK_RECT = false;

    public static final boolean drawIconId = false;

    // UDrawableオブジェクトの描画範囲をライン描画
    public static final boolean drawRectLine = false;

    // Select時にログを出力
    public static final boolean debugDAO = false;

    // テキストのベース座標に+を描画
    public static final boolean drawTextBaseLine = false;


    /**
     * Methods
     */
    /**
     * システムの全データクリア
     * アプリインストールと同じ状態に戻る
     */
    public static void clearSystemData() {
        // MySharedPref
        MySharedPref.clearAllData();

        // Realm
        // データベースを削除
        RealmManager.clearAll();

        // セーブデータを初期化
        RealmManager.getBackupFileDao().createInitialRecords();
        // デフォルト単語帳を追加
        PresetBookManager.getInstance().addDefaultBooks();

        MySharedPref.getInstance().writeBoolean(MySharedPref.InitializeKey, true);

    }
}
