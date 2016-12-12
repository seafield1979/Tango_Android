package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by shutaro on 2016/12/07.
 *
 * Shared Preferencesのラッパークラス
 * 設定等の情報を保存する
 */

public class MySharedPref {
    /**
     * Constants
     */
    public static final String TAG = "MySharedPref";

    // option key
    // 出題方法 false:英語->日本語 true:日本語->英語
    public static final String StudyOption1Key = "StudyOption1";
    // 出題順 false:順番通り true:ランダム
    public static final String StudyOption2Key = "StudyOption2";
    // 出題単語 false:全部  true:未収得
    public static final String StudyOption3Key = "StudyOption3";

    /**
     * Static varialbes
     */
    private static MySharedPref singleton;

    /**
     * Member variables
     */
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    /**
     * Constructor
     * Singleton
     */
    private MySharedPref(Context context) {
        mPrefs = context.getSharedPreferences("DataSave", context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    /**
     * 初期化処理
     * アプリ開始時に１回だけコールする
     * @param context
     */
    public static void init(Context context) {
        if (singleton == null) {
            singleton = new MySharedPref(context);
        }
    }
    public static MySharedPref getInstance() {
        if (singleton == null) {
            throw new NullPointerException("MySharePref.singleton is null!");
        }
        return singleton;
    }

    /**
     * Methods
     */
    /**
     * Write系
     */
    // String
    public static void writeString(String key, String value) {
        MySharedPref instance = getInstance();
        if (value == null) return;

        instance.mEditor.putString(key, value);
        instance.mEditor.apply();
    }
    // int
    public static void writeInt(String key, int value) {
        MySharedPref instance = getInstance();

        instance.mEditor.putInt(key, value);
        instance.mEditor.apply();
    }

    // boolean
    public static void writeBoolean(String key, boolean value) {
        MySharedPref instance = getInstance();

        instance.mEditor.putBoolean(key, value);
        instance.mEditor.apply();
    }

    /**
     * Read系
     */
    // String
    public static String readString(String key) {
        MySharedPref instance = getInstance();

        return instance.mPrefs.getString(key, "");
    }
    // int
    public static int readInt(String key) {
        MySharedPref instance = getInstance();

        return instance.mPrefs.getInt(key, 0);
    }
    // boolean
    public static boolean readBoolean(String key) {
        MySharedPref instance = getInstance();

        return instance.mPrefs.getBoolean(key, false);
    }

    /**
     * Shared Preferences の全てのデータを出力する
     */
    public void showAllData() {
        Map<String,?> keys = mPrefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            ULog.print( TAG, entry.getKey() + ": " + entry.getValue().toString() + "\n");
        }
    }
}
