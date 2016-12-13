package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Realmのオブジェクトを管理する
 */

public class RealmManager {
    public static final String TAG = "RealmManager";

    public static Realm realm;
    public static final int Version1 = 1;
    public static final int latestVersion = Version1;


    public static Realm getRealm() { return realm; }

    private static TangoCardDao cardDao;
    private static TangoBookDao bookDao;
    private static TangoItemPosDao itemPosDao;
    private static TangoCardHistoryDao cardHistoryDao;
    private static TangoBookHistoryDao bookHistoryDao;
    private static TangoTagDao tagDao;
    private static boolean initFlag;

    public static void initRealm(Context context) {
        if (initFlag) return;
        initFlag = true;

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .schemaVersion(latestVersion)
                .migration(new TangoMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();

        cardDao = new TangoCardDao(realm);
        bookDao = new TangoBookDao(realm);
        itemPosDao = new TangoItemPosDao(realm);
        cardHistoryDao = new TangoCardHistoryDao(realm);
        bookHistoryDao = new TangoBookHistoryDao(realm);
        tagDao = new TangoTagDao(realm);

    }

    public static TangoCardDao getCardDao() {
        return cardDao;
    }

    public static TangoBookDao getBookDao() {
        return bookDao;
    }

    public static TangoItemPosDao getItemPosDao() { return itemPosDao; }

    public static TangoCardHistoryDao getCardHistoryDao() {
        return cardHistoryDao;
    }

    public static TangoBookHistoryDao getBookHistoryDao() {
        return bookHistoryDao;
    }

    public static TangoTagDao getTagDao() {
        return tagDao;
    }

    public static void closeRealm() {
        realm.close();
    }

    /**
     * Methods
     */
    /**
     * 外部ストレージにRealmのコピーを作成する
     */
    public static void createCopyToStorage() {

        Realm r = Realm.getDefaultInstance();
        String fileName = "realm_copy.realm";

        // 外部ストレージ直下にrealm_copy.realmファイルを作成する
        //File f = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
        File f = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS) + "/" + fileName);

        ULog.print(TAG, f.getPath());

        if (f.exists()) {
            // 同一ファイル名のファイルが存在する場合エラーが発生するため、ファイルがすでに存在すれば削除する
            f.delete();
        }

        try {
            // 現時点での.realmファイルを指定のパスの位置にコピーする
            r.writeCopyTo(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        r.close();
    }
}
