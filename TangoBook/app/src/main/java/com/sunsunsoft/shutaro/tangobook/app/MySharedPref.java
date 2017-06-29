package com.sunsunsoft.shutaro.tangobook.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.sunsunsoft.shutaro.tangobook.study_card.StudyFilter;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyMode;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyOrder;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyType;
import com.sunsunsoft.shutaro.tangobook.util.ULog;

import java.util.Map;

/**
 * Created by shutaro on 2016/12/07.
 *
 * Shared Preferencesのラッパークラス
 * アプリの設定等の情報を保存する
 * データベース(Realm)を使うまでもないものはこれを使用する
 */



public class MySharedPref {
    /**
     * Constants
     */
    public static final String TAG = "MySharedPref";

    // 画面のズーム
    public static final String ScaleKey = "ScreenScale";

    // 初期化フラグ(初回起動時に１回だけ処理を行うために使用)
    public static final String InitializeKey = "InitializeFlag";

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

    // 自動バックアップ
    public static final String AutoBackup = "AutoBackup";

    // 編集ページ
    // メニューヘルプ(0:非表示 / 1:メニュー名を表示 / 2:メニューヘルプを表示)
    public static final String MenuHelpModeKey = "MenuHelpMode";

    // 学習する単語帳編集ページ
    public static final String StudyBookSortKey = "StudyBookSort";

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

    // ４択モードで正解以外のカードをどの範囲から取得するか
    public static final String StudyMode3OptionKey = "StudyMode3Select";

    // 単語入力モードの文字並び (false:アルファベット順 / true:ランダム)
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

    /**
     * 全てのデータを削除
     */
    public static void clearAllData() {
        delete(ScaleKey);
        delete(InitializeKey);
        delete(EditCardNameKey);
        delete(StudyModeKey);
        delete(StudyTypeKey);
        delete(StudyOrderKey);
        delete(StudyFilterKey);
        delete(MenuHelpModeKey);
        delete(StudyBookSortKey);
        delete(DefaultColorCardKey);
        delete(DefaultColorBookKey);
        delete(DefaultCardWordAKey);
        delete(DefaultCardWordBKey);
        delete(DefaultNameBookKey);
        delete(StudyMode3OptionKey);
        delete(StudyMode4OptionKey);
    }
}
