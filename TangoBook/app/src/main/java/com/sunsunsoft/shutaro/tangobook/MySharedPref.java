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
    SlideOne(R.string.study_mode_1),
    SlideMulti(R.string.study_mode_2),
    Choice4(R.string.study_mode_3),
    Input(R.string.study_mode_4),
    ;

    private final int strId;

    StudyMode(int strId) {
        this.strId = strId;
    }

    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyMode toEnum(int value) {
        if (value < StudyMode.values().length) {
            return StudyMode.values()[value];
        }
        return StudyMode.SlideOne;
    }
}

enum StudyType {
    EtoJ(R.string.study_type_1),
    JtoE(R.string.study_type_2)
    ;
    private final int strId;

    StudyType(int strId) {
        this.strId = strId;
    }

    public String getString() {
        return UResourceManager.getStringById(strId);
    }

    public static StudyType toEnum(int value) {
        if (value < StudyType.values().length) {
            return StudyType.values()[value];
        }
        return StudyType.EtoJ;
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
    // 単語編集ページのカードの名前表示 (false:英語 / true:日本語)
    public static final String EditCardNameKey = "EditCardName";

    // 出題方法
    public static final String StudyModeKey = "StudyMode";

    // 出題方法(英:日)
    public static final String StudyTypeKey = "StudyType";

    // 出題順
    public static final String StudyOrderKey = "StudyOrder";

    // 出題絞り込み
    public static final String StudyFilterKey = "StudyFilter";

    // バックアップ情報
    public static final String BackupInfoKey = "BackupInfo";
    public static final String AutoBackupInfoKey = "AutoBackupInfo";

    // 自動バックアップ
    public static final String AutoBackup = "AutoBackup";

    // 編集ページ
    // メニューヘルプ(0:非表示 / 1:メニュー名を表示 / 2:メニューヘルプを表示)
    public static final String MenuHelpModeKey = "MenuHelpMode";

    /*
    　オプション
     */
    // デフォルトのカード色
    public static final String DefaultColorCardKey = "DefaultColorCard";

    // デフォルトの単語帳色
    public static final String DefaultColorBookKey = "DefaultColorBook";

    // デフォルトのカード名
    public static final String DefaultCardWordAKey = "DefaultCardWordA";
    public static final String DefaultCardWordBKey = "DefaultCardWordB";

    // デフォルトの単語帳名
    public static final String DefaultNameBookKey = "DefaultNameBook";

    // NGカードを自動的に単語帳に追加
    public static final String AddNgCardToBookKey = "AddNgCardToBook";

    // 単語入力モードの文字並び
    public static final String StudyMode4OptionKey = "StudyMode4Seq";


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
    public static boolean getCardName() { return readBoolean(EditCardNameKey);}
    public static StudyMode getStudyMode() {
        return StudyMode.toEnum(readInt(StudyModeKey));
    }
    public static StudyType getStudyType() {
        return StudyType.toEnum(readInt(StudyTypeKey));
    }
    public static StudyOrder getStudyOrder() {
        return StudyOrder.toEnum(readInt(StudyOrderKey));
    }
    public static StudyFilter getStudyFilter() {
        return StudyFilter.toEnum(readInt(StudyFilterKey));
    }

    /**
     * Delete
     */
    public static void delete(String key) {
        MySharedPref instance = getInstance();
        instance.mEditor.remove(key);
        instance.mEditor.apply();
    }

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
    public static String readString(String key, String defaultValue) {
        MySharedPref instance = getInstance();

        return instance.mPrefs.getString(key, defaultValue);
    }


    // int
    public static int readInt(String key) {
        MySharedPref instance = getInstance();

        return instance.mPrefs.getInt(key, 0);
    }
    public static int readInt(String key, int defaultValue) {
        MySharedPref instance = getInstance();

        return instance.mPrefs.getInt(key, defaultValue);
    }

    // boolean
    public static boolean readBoolean(String key) {
        MySharedPref instance = getInstance();

        return instance.mPrefs.getBoolean(key, false);
    }

    public static boolean readBoolean(String key, boolean defaultValue) {
        MySharedPref instance = getInstance();

        return instance.mPrefs.getBoolean(key, defaultValue);
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
