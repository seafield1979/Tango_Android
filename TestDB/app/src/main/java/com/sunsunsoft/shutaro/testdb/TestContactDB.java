package com.sunsunsoft.shutaro.testdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/10/26.
 */

public class TestContactDB {

    Context mContext;

    public TestContactDB(Context context) {
        mContext = context;
    }

    public void openDB() {
        // ContactDbOpenHelperを生成
        ContactDbOpenHelper helper = new ContactDbOpenHelper(mContext);
        // 書き込み可能なSQLiteDatabaseインスタンスを取得
        SQLiteDatabase db = helper.getWritableDatabase();
        // データベースを閉じる
//        db.close();
//        helper.close();
    }
    /**
     * 全アイテムを取得する
     * @return
     */
    public List<ContactData> getAllData() {
        ContactDbOpenHelper helper = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<ContactData> list = new LinkedList<ContactData>();
        try {
            helper = new ContactDbOpenHelper(mContext);
            db = helper.getWritableDatabase();

            // Commentsテーブルのすべてのデータを取得
            cursor = db.query(Contact.TBNAME, null, null,
                    null, null, null,
                    Contact.NAME);
            // Cursorにデータが１件以上ある場合処理を行う
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // IDを取得
                    Integer _id = cursor.getInt(cursor.getColumnIndex(Contact._ID));

                    // 名前を取得
                    String name = cursor.getString(cursor
                            .getColumnIndex(Contact.NAME));
                    // 年齢を取得
                    int age = cursor.getInt(cursor.getColumnIndex(Contact.AGE));
                    list.add(new ContactData(_id, name, age));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            helper.close();
        }
        return list;
    }

    /**
     * アイテムを追加する
     * @param name
     * @param age
     */
    public void addData(String name, int age) {
        if (name == null) return;

        ContactDbOpenHelper helper = null;
        SQLiteDatabase db = null;
        try {
            helper = new ContactDbOpenHelper(mContext);
            db = helper.getWritableDatabase();

            // 生成するデータを格納するContentValuesを生成
            ContentValues values = new ContentValues();
            values.put(Contact.NAME, name);
            values.put(Contact.AGE, age);
            // 戻り値は生成されたデータの_IDが返却される
            long id = db.insert(Contact.TBNAME, null, values);
        } finally {
            db.close();
            helper.close();
        }
    }

    /**
     * アイテムを更新する
     * idsで指定されたidのデータをすべて更新する
     */
    public void updateData(int[] ids, String name, int age) {
        if (name == null || ids.length <= 0) return;

        ContactDbOpenHelper helper = null;
        SQLiteDatabase db = null;
        try {
            helper = new ContactDbOpenHelper(mContext);
            db = helper.getWritableDatabase();

            // idsをStringに変換
            StringBuffer strBuf = new StringBuffer();
            for (int i=0; i<ids.length; i++) {
                if (i > 0) {
                    strBuf.append(",");
                }
                strBuf.append(String.valueOf(ids[i]));
            }

            String query = "UPDATE " + Contact.TBNAME +
                    " SET " + Contact.NAME + "=\"" + name + "\"," + Contact.AGE + "=" + age +
                    " WHERE " + Contact._ID + " in (" + strBuf + ")";
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            cursor.close();
        } finally {
            db.close();
            helper.close();
        }
    }

}
