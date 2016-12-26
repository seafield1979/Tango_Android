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
// メニューのヘルプ表示
enum MenuHelpMode {
    None,       // 非表示
    Name,       // メニュー名を表示
    Help        // メニューヘルプを表示
    ;

    public static MenuHelpMode toEnum(int value) {
        if (value < values().length) {
            return values()[value];
        }
        return None;
    }
}

// 学習モード
enum StudyMode {
    SlideOneJ2E(false, R.string.study_mode_1),
    SlideOneE2J(true, R.string.study_mode_2),
    SlideMultiJ2E(false, R.string.study_mode_3),
    SlideMultiE2J(true, R.string.study_mode_4),
    SelectJ2E(false, R.string.study_mode_5),
    SelectE2J(true, R.string.study_mode_6),
    InputE(true, R.string.study_mode_7)
    ;

    // 英語の出題か？
    private final boolean english;
    private final int strId;

    StudyMode(boolean isEnglish, int strId) {
        english = isEnglish;
        this.strId = strId;
    }

    public boolean isEnglish() {
        return english;
    }
    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyMode toEnum(int value) {
        if (value < StudyMode.values().length) {
            return StudyMode.values()[value];
        }
        return StudyMode.SlideOneJ2E;
    }
}

// 並び順
enum StudyOrder {
    Normal(R.string.study_order_1),     // 通常（単語帳の並び順）
    Random(R.string.study_order_2)      // ランダムに並び替え
    ;

    private final int strId;
    StudyOrder(int strId) {
        this.strId = strId;
    }
    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyOrder toEnum(int value) {
        if (value < StudyOrder.values().length) {
            return StudyOrder.values()[value];
        }
        return StudyOrder.Normal;
    }
}

// 出題絞り込み
enum StudyFilter {
    All(R.string.study_filter_1),                // すべて出題
    NotLearned(R.string.study_filter_2),         // 未収得カードのみ
    ;

    private final int strId;
    StudyFilter(int strId) {
        this.strId = strId;
    }
    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyFilter toEnum(int value) {
        if (value < StudyFilter.values().length) {
            return StudyFilter.values()[value];
        }
        return StudyFilter.All;
    }
}

public class MySharedPref {
    /**
     * Constants
     */
    public static final String TAG = "MySharedPref";

    // option key

    // 出題方法
    public static final String StudyModeKey = "StudyMode";

    // 出題順
    public static final String StudyOrderKey = "StudyOrder";

    // 出題絞り込み
    public static final String StudyFilterKey = "StudyFilter";

    // Realmバックアップ保存場所
    public static final String RealmBackupPathKey = "RealmBackupPath";

    // Realmバックアップ日時
    public static final String RealmBackupDateKey = "RealmBackupDate";

    // 自動バックアップ
    public static final String RealmAutoBackup = "RealmAutoBackup";

    // 編集ページ
    // メニューヘルプ(0:非表示 / 1:メニュー名を表示 / 2:メニューヘルプを表示)
    public static final String MenuHelpModeKey = "MenuHelpMode";

    /**
     * Static varialbes
     */
    private static MySharedPref singleton;

    /**
     * Member variables
     */
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    // 設定値参照用
    private MenuHelpMode mMenuHelpMode;

    /**
     * Get/Set
     */
    public static MenuHelpMode getMenuHelpMode() {
        MySharedPref instance = getInstance();
        return instance.mMenuHelpMode;
    }
    public static void setMenuHelpMode(MenuHelpMode mode) {
        MySharedPref instance = getInstance();

        if (instance.mMenuHelpMode != mode) {
            instance.mMenuHelpMode = mode;
            MySharedPref.getInstance().writeInt(MySharedPref.MenuHelpModeKey, mode.ordinal());
        }
    }

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

            singleton.mMenuHelpMode = MenuHelpMode.toEnum(readInt(MenuHelpModeKey));
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
    public static void showAllData() {
        MySharedPref instance = getInstance();
        Map<String,?> keys = instance.mPrefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            ULog.print( TAG, entry.getKey() + ": " + entry.getValue().toString() + "\n");
        }
    }
}
