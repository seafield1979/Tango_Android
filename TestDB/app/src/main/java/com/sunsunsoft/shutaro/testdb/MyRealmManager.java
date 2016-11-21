package com.sunsunsoft.shutaro.testdb;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import android.content.Context;

/**
 * Realmのオブジェクトを管理する
 */

public class MyRealmManager {
    public static Realm realm;
    public static final int Version1 = 1;
    public static final int Version2 = 2;   // Add TangoBox
    public static final int Version21 = 21;   // remove TangoBox.studyTime field
    public static final int Version22 = 22;  // add primary key to TangoBox.id
    public static final int Version23 = 23;  // set nullAble
    public static final int Version30 = 30; // Add TangoCardInBook


    public static Realm getRealm() { return realm; }

    private static TangoCardDao cardDao;
    private static TangoBookDao bookDao;
    private static TangoBoxDao boxDao;
    private static TangoCardInBookDao cardInBookDao;
    private static TangoItemInBoxDao itemInBoxDao;

    public static void initRealm(Context context) {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .schemaVersion(Version30)
                .migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();

        cardDao = new TangoCardDao(realm);
        bookDao = new TangoBookDao(realm);
        boxDao = new TangoBoxDao(realm);
        cardInBookDao = new TangoCardInBookDao(realm);
        itemInBoxDao = new TangoItemInBoxDao(realm);
    }

    public static TangoCardDao getCardDao() {
        return cardDao;
    }

    public static TangoBookDao getBookDao() {
        return bookDao;
    }

    public static TangoBoxDao getBoxDao() {
        return boxDao;
    }

    public static TangoCardInBookDao getCardBookDao() {
        return cardInBookDao;
    }

    public static TangoItemInBoxDao getItemInBoxDao() {
        return itemInBoxDao;
    }

    public static void closeRealm() {
        realm.close();
    }
}
