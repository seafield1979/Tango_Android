package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Realmのオブジェクトを管理する
 */

public class RealmManager {
    public static Realm realm;
    public static final int Version1 = 1;
    public static final int latestVersion = Version1;


    public static Realm getRealm() { return realm; }

    private static TangoCardDao cardDao;
    private static TangoBookDao bookDao;
    private static TangoItemPosDao itemPosDao;
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
    }

    public static TangoCardDao getCardDao() {
        return cardDao;
    }

    public static TangoBookDao getBookDao() {
        return bookDao;
    }

    public static TangoItemPosDao getItemPosDao() { return itemPosDao; }

    public static void closeRealm() {
        realm.close();
    }
}
