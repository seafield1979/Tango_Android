package com.sunsunsoft.shutaro.testdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Contactテーブルを作成する
 */
public class ContactDbOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "ContactDbOpenHelper";

    static final String DATABASE_NAME = "contact.db";
    static final int DATABASE_VERSION = 1;

    public ContactDbOpenHelper(Context context) {
        // データベースファイル名とバージョンを指定しSQLiteOpenHelperクラスを初期化
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "ContactDbOpenHelperのコンストラクタが呼ばれました");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(TAG, "ContactDbOpenHelper.onCreateが呼ばれました");
        // Contactテーブルを生成
        // @formatter:off
        database.execSQL("CREATE TABLE "
                + Contact.TBNAME + "("
                + Contact._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contact.NAME + " TEXT NOT NULL, "
                + Contact.AGE + " INTEGER " + ");");
        // @formatter:on
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "ContactDbOpenHelper.onUpgradeが呼ばれました");
        // Contactテーブルを再定義するため現在のテーブルを削除
        db.execSQL("DROP TABLE IF EXISTS " + Contact.TBNAME);
        onCreate(db);
    }

}