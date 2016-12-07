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
    public void writeString(String key, String value) {
        if (value == null) return;

        mEditor.putString(key, value);
        mEditor.apply();
    }
    // int
    public void writeInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    // boolean
    public void writeBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    /**
     * Read系
     */
    // String
    public String readString(String key) {
        return mPrefs.getString(key, "");
    }
    // int
    public int readInt(String key) {
        return mPrefs.getInt(key, 0);
    }
    // boolean
    public boolean readBoolean(String key) {
        return mPrefs.getBoolean(key, false);
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
